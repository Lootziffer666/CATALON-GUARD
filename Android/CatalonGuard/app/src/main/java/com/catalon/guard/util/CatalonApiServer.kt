package com.catalon.guard.util

import com.catalon.guard.domain.model.ChatMessage
import com.catalon.guard.domain.usecase.ChatUseCase
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.*
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalonApiServer @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val gson: Gson
) : NanoHTTPD(4141) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun serve(session: IHTTPSession): Response {
        if (session.method != Method.POST) {
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "")
        }
        if (session.uri != "/v1/chat/completions") {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json",
                """{"error":{"message":"Only POST /v1/chat/completions is supported","type":"not_found"}}""")
        }

        return try {
            val body = HashMap<String, String>()
            session.parseBody(body)
            val postData = body["postData"] ?: body["content"] ?: ""
            val request = gson.fromJson(postData, OpenAiRequest::class.java)
                ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json",
                    """{"error":{"message":"Invalid JSON body","type":"bad_request"}}""")

            val messages = request.messages.map {
                ChatMessage(id = UUID.randomUUID().toString(), role = it.role, content = it.content)
            }
            val sessionId = "api_${System.currentTimeMillis()}"

            if (request.stream == true) {
                streamResponse(messages, sessionId)
            } else {
                blockingResponse(messages, sessionId)
            }
        } catch (e: Exception) {
            newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json",
                """{"error":{"message":"${e.message?.replace("\"", "'")}","type":"internal_error"}}""")
        }
    }

    private fun streamResponse(messages: List<ChatMessage>, sessionId: String): Response {
        val pipeOut = PipedOutputStream()
        val pipeIn = PipedInputStream(pipeOut, 8192)
        val writer = pipeOut.bufferedWriter()

        scope.launch {
            try {
                chatUseCase.chat(messages, sessionId).collect { result ->
                    when (result) {
                        is ChatUseCase.ChatResult.Token -> {
                            writer.write(buildSseChunk(result.text))
                            writer.flush()
                        }
                        is ChatUseCase.ChatResult.Complete -> {
                            writer.write("data: [DONE]\n\n")
                            writer.flush()
                        }
                        is ChatUseCase.ChatResult.Error -> {
                            val errJson = """{"error":{"message":"${result.message.replace("\"", "'")}","type":"provider_error"}}"""
                            writer.write("data: $errJson\n\n")
                            writer.flush()
                        }
                        else -> {}
                    }
                }
            } finally {
                runCatching { writer.close() }
            }
        }

        return newChunkedResponse(Response.Status.OK, "text/event-stream", pipeIn)
    }

    private fun blockingResponse(messages: List<ChatMessage>, sessionId: String): Response {
        val accumulated = StringBuilder()
        var modelId = "catalon-guard"

        runBlocking {
            chatUseCase.chat(messages, sessionId).collect { result ->
                when (result) {
                    is ChatUseCase.ChatResult.Token -> {
                        accumulated.append(result.text)
                        modelId = result.providerId
                    }
                    else -> {}
                }
            }
        }

        val content = accumulated.toString()
            .replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "")
        val json = """{"id":"catg-${System.currentTimeMillis()}","object":"chat.completion","model":"$modelId","choices":[{"index":0,"message":{"role":"assistant","content":"$content"},"finish_reason":"stop"}],"usage":{"prompt_tokens":0,"completion_tokens":${accumulated.length / 4},"total_tokens":${accumulated.length / 4}}}"""
        return newFixedLengthResponse(Response.Status.OK, "application/json", json)
    }

    private fun buildSseChunk(token: String): String {
        val escaped = token.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "")
        return "data: {\"id\":\"catg\",\"object\":\"chat.completion.chunk\",\"choices\":[{\"delta\":{\"content\":\"$escaped\"},\"index\":0,\"finish_reason\":null}]}\n\n"
    }

    fun startServer() {
        if (!isAlive) start(SOCKET_READ_TIMEOUT, false)
    }

    fun stopServer() {
        if (isAlive) stop()
        scope.coroutineContext.cancelChildren()
    }

    data class OpenAiRequest(
        val model: String = "",
        val messages: List<MsgDto> = emptyList(),
        val stream: Boolean? = null,
        @SerializedName("max_tokens") val maxTokens: Int? = null
    )

    data class MsgDto(val role: String = "user", val content: String = "")
}
