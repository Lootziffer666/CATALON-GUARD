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
        val reader = body.charStream().buffered()
        reader.use { br ->
            br.forEachLine { line ->
                if (!line.startsWith("data: ")) return@forEachLine
                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") return@forEachLine
                if (data.isBlank()) return@forEachLine
                try {
                    val chunk = gson.fromJson(data, StreamChunk::class.java)
                    val token = chunk?.choices?.firstOrNull()?.delta?.content
                    if (token != null && token.isNotEmpty()) emit(token)
                } catch (_: JsonSyntaxException) {
                    // Skip malformed lines (common with some providers)
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
