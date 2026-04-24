package com.catalon.guard.util

import com.catalon.guard.data.remote.dto.StreamChunk
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody

object SseStreamParser {

    fun parseStream(body: ResponseBody, gson: Gson): Flow<String> = flow {
        body.charStream().buffered().use { br ->
            while (true) {
                val line = br.readLine() ?: break
                if (!line.startsWith("data: ")) continue

                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") continue
                if (data.isBlank()) continue

                try {
                    val chunk = gson.fromJson(data, StreamChunk::class.java)
                    val token = chunk?.choices?.firstOrNull()?.delta?.content
                    if (!token.isNullOrEmpty()) {
                        emit(token)
                    }
                } catch (_: JsonSyntaxException) {
                    // Skip malformed lines
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
