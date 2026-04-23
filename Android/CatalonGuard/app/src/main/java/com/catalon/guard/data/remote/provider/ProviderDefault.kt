package com.catalon.guard.data.remote.provider

data class ModelDefault(
    val modelId: String,
    val displayName: String = modelId,
    val contextWindow: Int,
    val maxOutput: Int,
    val rpmLimit: Int = Int.MAX_VALUE,
    val rpdLimit: Int = Int.MAX_VALUE,
    val supportsVision: Boolean = false,
    val supportsReasoning: Boolean = false
)

data class ProviderDefault(
    val id: String,
    val name: String,
    val baseUrl: String,
    val models: List<ModelDefault>,
    val rpmLimit: Int,
    val rpdLimit: Int,
    val contextWindow: Int,
    val maxOutput: Int,
    val tier: Int,
    val isByok: Boolean = false,
    val authType: String = "API_KEY",
    val notes: String = ""
)
