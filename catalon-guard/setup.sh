#!/bin/bash
# Catalon-Guard Environment Setup
# Run this once to install dependencies and configure environment

set -e

echo "=== Catalon-Guard Environment Setup ==="

# Check Python
if ! command -v python3 &> /dev/null; then
    echo "Python3 is required but not installed."
    exit 1
fi

# Check pip
if ! command -v pip &> /dev/null; then
    echo "pip is required but not installed."
    exit 1
fi

echo "Installing LiteLLM with proxy dependencies..."
pip install 'litellm[proxy]' requests

echo ""
echo "Creating environment template..."
cat > .env.template << 'EOF'
# ============================================================================
# Catalon-Guard Environment Variables
# Copy this to .env and fill in your values
# ============================================================================

# Required: Google Cloud
export GOOGLE_PROJECT_ID="your-google-project-id"
export GOOGLE_LOCATION="us-central1"

# Required: Path to your Vertex AI Service Account JSON
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/service-account.json"

# Required: OpenRouter (free models)
export OPENROUTER_API_KEY="sk-or-v1-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"

# Required: LiteLLM Master Key (for authentication)
export LITELLM_MASTER_KEY="sk-catalon-safe-key"

# Optional: Ollama (local models)
export OLLAMA_HOST="http://localhost:11434"
EOF

echo "Created .env.template"
echo ""
echo "=== Setup Complete ==="
echo ""
echo "Next steps:"
echo "1. Copy .env.template to .env"
echo "2. Fill in your API keys"
echo "3. Run: source .env"
echo "4. Run: ./run_proxy.sh"
echo ""
echo "Monitor with: python guard_stats.py --api-key \$LITELLM_MASTER_KEY"