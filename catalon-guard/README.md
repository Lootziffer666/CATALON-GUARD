# Catalon-Guard - V1.0 Infrastruktur & Kosten-Schild

## Quick Start

```bash
# 1. Install dependencies
cd catalon-guard
chmod +x setup.sh run_proxy.sh
./setup.sh

# 2. Configure environment
cp .env.template .env
# Edit .env with your API keys

# 3. Start proxy
source .env
./run_proxy.sh
```

## Structure

| File | Purpose |
|------|---------|
| `config.yaml` | Main LiteLLM configuration |
| `run_proxy.sh` | Start the proxy server |
| `guard_stats.py` | Real-time budget monitoring |
| `model_manager.py` | Add/remove models dynamically |
| `setup.sh` | First-time setup |

## Models (Pre-configured)

### Vertex AI (your $250 credit)
- `gpt-4o` → Gemini 1.5 Pro-002 (Mayor)
- `gpt-4.5` → Gemini 2.0 Flash
- `gpt-4o-mini` → Gemini 1.5 Flash-002 (Polecat)
- `gpt-4o-mini-2` → Gemini 2.0 Flash-exp

### OpenRouter (Free Tier)
- `refinery` → nomic-ai/mimo-v2-pro
- `refinery-deepseek` → deepseek/deepseek-chat
- `qwen-coder` → qwen/qwen-2.5-coder-32b-instruct
- `phi-mini` → microsoft/phi-4-mini
- `nous-hermes` → nousresearch/nous-hermes-2-mistral-7b-dpo
- `claude-sonnet` → anthropic/claude-3-sonnet

## Adding New Models

### Method 1: Edit config.yaml (requires restart)

```yaml
model_list:
  - model_name: my-new-model
    litellm_params:
      model: provider/model-name
      api_key: "${MY_API_KEY}"
      # ... other params
```

Then restart: `./run_proxy.sh`

### Method 2: Dynamic (no restart)

Create a model config file `my_model.json`:
```json
{
  "model_name": "my-model",
  "litellm_params": {
    "model": "openai/gpt-4o",
    "api_key": "sk-..."
  }
}
```

Add it:
```bash
python model_manager.py add my_model.json
```

### Method 3: Via API

```bash
curl -X POST http://localhost:4000/model/add \
  -H "Authorization: Bearer sk-catalon-safe-key" \
  -H "Content-Type: application/json" \
  -d '{"model_name":"new-model","litellm_params":{"model":"..."}}'
```

## Adding Custom Providers

### Ollama (Local)
```yaml
- model_name: ollama-llama3
  litellm_params:
    model: ollama/llama3
    api_base: http://localhost:11434
```

### OpenAI Compatible
```yaml
- model_name: custom-gpt
  litellm_params:
    model: openai/custom-model
    api_key: "${OPENAI_KEY}"
    api_base: https://your-endpoint.com/v1
```

### Anthropic
```yaml
- model_name: claude-opus
  litellm_params:
    model: anthropic/claude-3-opus
    api_key: "${ANTHROPIC_KEY}"
```

### Azure OpenAI
```yaml
- model_name: azure-gpt4
  litellm_params:
    model: azure/gpt-4
    api_key: "${AZURE_KEY}"
    api_base: https://your-resource.openai.azure.com
```

## API Usage

```python
import openai
client = openai.OpenAI(
    api_key="sk-catalon-safe-key",
    base_url="http://localhost:4000/v1"
)

response = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "Hello!"}]
)
```

## Budget Protection

- **Hard limit**: $5/day (configurable in config.yaml)
- **Max tokens/request**: 8,192 (prevents runaway loops)
- **80% warning**: Console alert when budget reaches 80%
- **100% block**: Returns 429 Error - all requests blocked

## Monitoring

```bash
# Real-time dashboard
python guard_stats.py --api-key sk-catalon-safe-key

# LiteLLM Admin UI
# Open: http://localhost:4000/ui
```

## Troubleshooting

| Error | Solution |
|-------|----------|
| Connection refused | Proxy not running. Run `./run_proxy.sh` |
| 401 Unauthorized | Check `LITELLM_MASTER_KEY` in .env |
| 429 Budget exceeded | Wait for reset or increase limit in config.yaml |
| Vertex error | Check `GOOGLE_APPLICATION_CREDENTIALS` path |
| OpenRouter error | Verify `OPENROUTER_API_KEY` in .env |