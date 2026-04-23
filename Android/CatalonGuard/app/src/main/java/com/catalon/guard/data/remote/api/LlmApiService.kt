package com.catalon.guard.data.remote.api

import com.catalon.guard.data.remote.dto.ChatRequest
import com.catalon.guard.data.remote.dto.ChatResponse
import com.catalon.guard.data.remote.dto.ModelsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LlmApiService {

    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("X-Target-Base-Url") baseUrl: String,
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @Streaming
    @POST("v1/chat/completions")
    fun chatCompletionStream(
        @Header("X-Target-Base-Url") baseUrl: String,
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Call<ResponseBody>

    @GET("v1/models")
    suspend fun listModels(
        @Header("X-Target-Base-Url") baseUrl: String,
        @Header("Authorization") authorization: String
    ): Response<ModelsResponse>
}
