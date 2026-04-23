package com.catalon.guard.data.remote.provider

object ProviderRegistry {

    val defaults: List<ProviderDefault> = listOf(

        // ── Tier 1: Beste Qualität ─────────────────────────────────────────────

        ProviderDefault(
            id = "gemini",
            name = "Google Gemini",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai/",
            models = listOf(
                ModelDefault("gemini-2.5-flash", "Gemini 2.5 Flash",
                    contextWindow = 1_000_000, maxOutput = 65_536,
                    rpmLimit = 10, rpdLimit = 250,
                    supportsVision = true),
                ModelDefault("gemini-2.5-flash-lite", "Gemini 2.5 Flash-Lite",
                    contextWindow = 1_000_000, maxOutput = 65_536,
                    rpmLimit = 15, rpdLimit = 1_000,
                    supportsVision = true)
            ),
            rpmLimit = 10, rpdLimit = 250,
            contextWindow = 1_000_000, maxOutput = 65_536,
            tier = 1
        ),

        ProviderDefault(
            id = "github_models",
            name = "GitHub Models",
            baseUrl = "https://models.inference.ai.azure.com/",
            models = listOf(
                ModelDefault("gpt-4.1", "GPT-4.1",
                    contextWindow = 1_000_000, maxOutput = 32_768,
                    rpmLimit = 10, rpdLimit = 50),
                ModelDefault("gpt-4.1-mini", "GPT-4.1 Mini",
                    contextWindow = 1_000_000, maxOutput = 32_768,
                    rpmLimit = 15, rpdLimit = 150),
                ModelDefault("gpt-4o", "GPT-4o",
                    contextWindow = 128_000, maxOutput = 16_384,
                    rpmLimit = 10, rpdLimit = 50, supportsVision = true),
                ModelDefault("o3-mini", "o3-mini",
                    contextWindow = 200_000, maxOutput = 100_000,
                    rpmLimit = 10, rpdLimit = 50, supportsReasoning = true),
                ModelDefault("o4-mini", "o4-mini",
                    contextWindow = 200_000, maxOutput = 100_000,
                    rpmLimit = 10, rpdLimit = 50, supportsReasoning = true)
            ),
            rpmLimit = 10, rpdLimit = 50,
            contextWindow = 1_000_000, maxOutput = 32_768,
            tier = 1
        ),

        // ── Tier 2: Gute Qualität, höhere Limits ──────────────────────────────

        ProviderDefault(
            id = "groq",
            name = "Groq",
            baseUrl = "https://api.groq.com/openai/",
            models = listOf(
                ModelDefault("llama-3.3-70b-versatile", "Llama 3.3 70B",
                    contextWindow = 131_000, maxOutput = 32_768),
                ModelDefault("llama-3.1-8b-instant", "Llama 3.1 8B",
                    contextWindow = 131_000, maxOutput = 131_000),
                ModelDefault("llama-4-scout-17b-16e-instruct", "Llama 4 Scout",
                    contextWindow = 131_000, maxOutput = 8_192, supportsVision = true),
                ModelDefault("llama-4-maverick-17b-128e-instruct", "Llama 4 Maverick",
                    contextWindow = 131_000, maxOutput = 8_192,
                    rpmLimit = 15, rpdLimit = 500, supportsVision = true),
                ModelDefault("kimi-k2-instruct", "Kimi K2",
                    contextWindow = 262_000, maxOutput = 262_000),
                ModelDefault("deepseek-r1-distill-llama-70b", "DeepSeek R1 Distill",
                    contextWindow = 131_000, maxOutput = 16_384, supportsReasoning = true)
            ),
            rpmLimit = 30, rpdLimit = 14_400,
            contextWindow = 262_000, maxOutput = 32_768,
            tier = 2
        ),

        ProviderDefault(
            id = "cerebras",
            name = "Cerebras",
            baseUrl = "https://api.cerebras.ai/v1/",
            models = listOf(
                ModelDefault("llama3.1-8b", "Llama 3.1 8B",
                    contextWindow = 128_000, maxOutput = 8_192),
                ModelDefault("gpt-oss-120b", "GPT OSS 120B",
                    contextWindow = 128_000, maxOutput = 8_192),
                ModelDefault("qwen-3-235b-a22b-instruct-2507", "Qwen3 235B",
                    contextWindow = 131_000, maxOutput = 8_192)
            ),
            rpmLimit = 30, rpdLimit = 14_400,
            contextWindow = 131_000, maxOutput = 8_192,
            tier = 2
        ),

        ProviderDefault(
            id = "mistral",
            name = "Mistral AI",
            baseUrl = "https://api.mistral.ai/",
            models = listOf(
                ModelDefault("mistral-small-2501", "Mistral Small 4",
                    contextWindow = 256_000, maxOutput = 256_000, supportsVision = true),
                ModelDefault("mistral-medium-2505", "Mistral Medium 3",
                    contextWindow = 128_000, maxOutput = 128_000),
                ModelDefault("mistral-large-2411", "Mistral Large 3",
                    contextWindow = 256_000, maxOutput = 256_000),
                ModelDefault("open-mistral-nemo", "Mistral Nemo 12B",
                    contextWindow = 128_000, maxOutput = 128_000),
                ModelDefault("codestral-2501", "Codestral",
                    contextWindow = 256_000, maxOutput = 256_000)
            ),
            rpmLimit = 60, rpdLimit = Int.MAX_VALUE,
            contextWindow = 256_000, maxOutput = 256_000,
            tier = 2
        ),

        ProviderDefault(
            id = "cohere",
            name = "Cohere",
            baseUrl = "https://api.cohere.com/compatibility/",
            models = listOf(
                ModelDefault("command-a-03-2025", "Command A 111B",
                    contextWindow = 256_000, maxOutput = 4_096),
                ModelDefault("command-r-plus", "Command R+",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("command-r", "Command R",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("command-r7b-12-2024", "Command R7B",
                    contextWindow = 128_000, maxOutput = 4_096)
            ),
            rpmLimit = 20, rpdLimit = Int.MAX_VALUE,
            contextWindow = 256_000, maxOutput = 4_096,
            tier = 2
        ),

        ProviderDefault(
            id = "nvidia_nim",
            name = "NVIDIA NIM",
            baseUrl = "https://integrate.api.nvidia.com/v1/",
            models = listOf(
                ModelDefault("deepseek-ai/deepseek-r1", "DeepSeek R1",
                    contextWindow = 128_000, maxOutput = 163_000, supportsReasoning = true),
                ModelDefault("nvidia/llama-3.1-nemotron-ultra-253b-v1", "Nemotron Ultra 253B",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("nvidia/nemotron-3-super-120b-a12b", "Nemotron Super 120B",
                    contextWindow = 262_000, maxOutput = 262_000),
                ModelDefault("meta/llama-3.1-405b-instruct", "Llama 3.1 405B",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("qwen/qwen2.5-72b-instruct", "Qwen 2.5 72B",
                    contextWindow = 128_000, maxOutput = 8_192)
            ),
            rpmLimit = 40, rpdLimit = Int.MAX_VALUE,
            contextWindow = 262_000, maxOutput = 262_000,
            tier = 2
        ),

        // ── Tier 3: Gute Free-Tier Limits ─────────────────────────────────────

        ProviderDefault(
            id = "openrouter",
            name = "OpenRouter",
            baseUrl = "https://openrouter.ai/api/",
            models = listOf(
                ModelDefault("deepseek/deepseek-r1-0528:free", "DeepSeek R1 (free)",
                    contextWindow = 163_000, maxOutput = 163_000, supportsReasoning = true),
                ModelDefault("deepseek/deepseek-chat-v3-0324:free", "DeepSeek V3 (free)",
                    contextWindow = 163_000, maxOutput = 163_000),
                ModelDefault("qwen/qwen3.6-plus:free", "Qwen3.6 Plus (free)",
                    contextWindow = 1_000_000, maxOutput = 65_536),
                ModelDefault("meta-llama/llama-4-scout:free", "Llama 4 Scout (free)",
                    contextWindow = 10_000_000, maxOutput = 16_384, supportsVision = true),
                ModelDefault("openai/gpt-oss-120b:free", "GPT OSS 120B (free)",
                    contextWindow = 131_000, maxOutput = 131_000),
                ModelDefault("nvidia/nemotron-3-super-120b-a12b:free", "Nemotron 120B (free)",
                    contextWindow = 262_000, maxOutput = 32_768)
            ),
            rpmLimit = 20, rpdLimit = 200,
            contextWindow = 10_000_000, maxOutput = 163_000,
            tier = 3
        ),

        ProviderDefault(
            id = "siliconflow",
            name = "SiliconFlow",
            baseUrl = "https://api.siliconflow.cn/v1/",
            models = listOf(
                ModelDefault("Qwen/Qwen3-8B", "Qwen3 8B",
                    contextWindow = 131_000, maxOutput = 131_000),
                ModelDefault("deepseek-ai/DeepSeek-R1-0528-Qwen3-8B", "DeepSeek R1 Qwen3 8B",
                    contextWindow = 33_000, maxOutput = 16_384, supportsReasoning = true),
                ModelDefault("deepseek-ai/DeepSeek-R1-Distill-Qwen-7B", "DeepSeek R1 Qwen 7B",
                    contextWindow = 131_000, maxOutput = 8_192, supportsReasoning = true),
                ModelDefault("THUDM/glm-4-9b-chat", "GLM-4 9B",
                    contextWindow = 32_000, maxOutput = 32_000),
                ModelDefault("THUDM/GLM-4.1V-9B-Thinking", "GLM-4.1V 9B",
                    contextWindow = 66_000, maxOutput = 66_000, supportsVision = true)
            ),
            rpmLimit = 1_000, rpdLimit = Int.MAX_VALUE,
            contextWindow = 131_000, maxOutput = 131_000,
            tier = 3
        ),

        ProviderDefault(
            id = "llm7",
            name = "LLM7.io",
            baseUrl = "https://api.llm7.io/v1/",
            models = listOf(
                ModelDefault("deepseek-r1-0528", "DeepSeek R1",
                    contextWindow = 128_000, maxOutput = 8_192, supportsReasoning = true),
                ModelDefault("deepseek-v3-0324", "DeepSeek V3",
                    contextWindow = 128_000, maxOutput = 8_192),
                ModelDefault("gemini-2.5-flash-lite", "Gemini 2.5 Flash-Lite",
                    contextWindow = 1_000_000, maxOutput = 65_536, supportsVision = true),
                ModelDefault("gpt-4o-mini", "GPT-4o Mini",
                    contextWindow = 128_000, maxOutput = 8_192, supportsVision = true),
                ModelDefault("mistral-small-3.1-24b", "Mistral Small 3.1",
                    contextWindow = 32_000, maxOutput = 8_192)
            ),
            rpmLimit = 30, rpdLimit = Int.MAX_VALUE,
            contextWindow = 1_000_000, maxOutput = 65_536,
            tier = 3
        ),

        ProviderDefault(
            id = "kilocode",
            name = "Kilo Code",
            baseUrl = "https://api.kilocode.ai/v1/",
            models = listOf(
                ModelDefault("nvidia/nemotron-3-super-120b-a12b:free", "Nemotron 120B (free)",
                    contextWindow = 262_000, maxOutput = 32_768),
                ModelDefault("x-ai/grok-code-fast-1:optimized:free", "Grok Code Fast (free)",
                    contextWindow = 131_000, maxOutput = 8_192),
                ModelDefault("arcee-ai/trinity-large-thinking:free", "Trinity Large Thinking (free)",
                    contextWindow = 131_000, maxOutput = 32_768, supportsReasoning = true)
            ),
            rpmLimit = 200, rpdLimit = Int.MAX_VALUE,
            contextWindow = 262_000, maxOutput = 32_768,
            tier = 3
        ),

        // ── Tier 4: Niedrige Limits / Fallback ────────────────────────────────

        ProviderDefault(
            id = "huggingface",
            name = "Hugging Face",
            baseUrl = "https://router.huggingface.co/hf-inference/",
            models = listOf(
                ModelDefault("meta-llama/Meta-Llama-3.1-8B-Instruct", "Llama 3.1 8B",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("mistralai/Mistral-7B-Instruct-v0.3", "Mistral 7B",
                    contextWindow = 32_000, maxOutput = 4_096),
                ModelDefault("mistralai/Mixtral-8x7B-Instruct-v0.1", "Mixtral 8x7B",
                    contextWindow = 32_000, maxOutput = 4_096),
                ModelDefault("microsoft/Phi-3.5-mini-instruct", "Phi-3.5 Mini",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("Qwen/Qwen2.5-7B-Instruct", "Qwen 2.5 7B",
                    contextWindow = 131_000, maxOutput = 4_096)
            ),
            rpmLimit = Int.MAX_VALUE, rpdLimit = 1_000,
            contextWindow = 131_000, maxOutput = 4_096,
            tier = 4
        ),

        ProviderDefault(
            id = "zai",
            name = "Z.AI",
            baseUrl = "https://open.bigmodel.cn/api/paas/v4/",
            models = listOf(
                ModelDefault("glm-4-air-250414", "GLM-4.7 Flash",
                    contextWindow = 200_000, maxOutput = 128_000),
                ModelDefault("glm-4.5-flash", "GLM-4.5 Flash",
                    contextWindow = 128_000, maxOutput = 8_192),
                ModelDefault("glm-4v-flash", "GLM-4.6V Flash",
                    contextWindow = 128_000, maxOutput = 4_096, supportsVision = true)
            ),
            rpmLimit = 1, rpdLimit = Int.MAX_VALUE,
            contextWindow = 200_000, maxOutput = 128_000,
            tier = 4,
            notes = "Concurrent limit: 1 request at a time"
        ),

        ProviderDefault(
            id = "ollama_cloud",
            name = "Ollama Cloud",
            baseUrl = "https://api.ollama.ai/v1/",
            models = listOf(
                ModelDefault("llama3.1:cloud", "Llama 3.1 (Cloud)",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("deepseek-r1:cloud", "DeepSeek R1 (Cloud)",
                    contextWindow = 128_000, maxOutput = 4_096, supportsReasoning = true),
                ModelDefault("qwen2.5:cloud", "Qwen 2.5 (Cloud)",
                    contextWindow = 128_000, maxOutput = 4_096),
                ModelDefault("mistral:cloud", "Mistral (Cloud)",
                    contextWindow = 32_000, maxOutput = 4_096)
            ),
            rpmLimit = 10, rpdLimit = Int.MAX_VALUE,
            contextWindow = 128_000, maxOutput = 4_096,
            tier = 4,
            notes = "Session/weekly limits not publicly documented"
        ),

        // ── BYOK: Kostenpflichtige Provider ───────────────────────────────────

        ProviderDefault(
            id = "vertex_ai",
            name = "Google Vertex AI",
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai/",
            models = listOf(
                ModelDefault("gemini-2.5-pro", "Gemini 2.5 Pro",
                    contextWindow = 2_000_000, maxOutput = 65_536, supportsVision = true),
                ModelDefault("gemini-2.0-flash", "Gemini 2.0 Flash",
                    contextWindow = 1_000_000, maxOutput = 65_536, supportsVision = true),
                ModelDefault("gemini-1.5-pro-002", "Gemini 1.5 Pro",
                    contextWindow = 2_000_000, maxOutput = 8_192, supportsVision = true)
            ),
            rpmLimit = 60, rpdLimit = Int.MAX_VALUE,
            contextWindow = 2_000_000, maxOutput = 65_536,
            tier = 1, isByok = true,
            authType = "VERTEX",
            notes = "Supports AI Studio API key or Service Account JSON. Use your \$250 credit!"
        ),

        ProviderDefault(
            id = "anthropic",
            name = "Anthropic",
            baseUrl = "https://api.anthropic.com/v1/",
            models = listOf(
                ModelDefault("claude-opus-4-5", "Claude Opus 4.5",
                    contextWindow = 200_000, maxOutput = 32_768),
                ModelDefault("claude-sonnet-4-5", "Claude Sonnet 4.5",
                    contextWindow = 200_000, maxOutput = 32_768),
                ModelDefault("claude-haiku-4-5", "Claude Haiku 4.5",
                    contextWindow = 200_000, maxOutput = 32_768)
            ),
            rpmLimit = 50, rpdLimit = Int.MAX_VALUE,
            contextWindow = 200_000, maxOutput = 32_768,
            tier = 1, isByok = true
        ),

        ProviderDefault(
            id = "openai",
            name = "OpenAI",
            baseUrl = "https://api.openai.com/",
            models = listOf(
                ModelDefault("gpt-4o", "GPT-4o",
                    contextWindow = 128_000, maxOutput = 16_384, supportsVision = true),
                ModelDefault("gpt-4o-mini", "GPT-4o Mini",
                    contextWindow = 128_000, maxOutput = 16_384, supportsVision = true),
                ModelDefault("o3-mini", "o3-mini",
                    contextWindow = 200_000, maxOutput = 100_000, supportsReasoning = true)
            ),
            rpmLimit = 60, rpdLimit = Int.MAX_VALUE,
            contextWindow = 128_000, maxOutput = 16_384,
            tier = 1, isByok = true
        )
    )
}
