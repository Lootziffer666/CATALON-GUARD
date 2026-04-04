"use client";

import { FormEvent, useMemo, useState } from "react";

type Endpoint = {
  id: string;
  alias: string;
  model: string;
  apiBase: string;
  apiKeyEnv: string;
};

const starter: Endpoint[] = [
  {
    id: "1",
    alias: "custom-gpt",
    model: "openai/gpt-4o-mini",
    apiBase: "https://my-endpoint.example.com/v1",
    apiKeyEnv: "OPENAI_KEY",
  },
  {
    id: "2",
    alias: "support-bot",
    model: "openai/gpt-4.1-mini",
    apiBase: "https://support-llm.example.com/v1",
    apiKeyEnv: "SUPPORT_LLM_KEY",
  },
];

export default function MultiCustomOpenAIWorkbench() {
  const [endpoints, setEndpoints] = useState<Endpoint[]>(starter);
  const [alias, setAlias] = useState("");
  const [model, setModel] = useState("openai/gpt-4o-mini");
  const [apiBase, setApiBase] = useState("https://");
  const [apiKeyEnv, setApiKeyEnv] = useState("OPENAI_KEY");

  function addEndpoint(e: FormEvent) {
    e.preventDefault();
    if (!alias.trim() || !model.trim() || !apiBase.trim() || !apiKeyEnv.trim()) return;

    const item: Endpoint = {
      id: crypto.randomUUID(),
      alias: alias.trim(),
      model: model.trim(),
      apiBase: apiBase.trim(),
      apiKeyEnv: apiKeyEnv.trim(),
    };

    setEndpoints((prev) => [item, ...prev]);
    setAlias("");
    setModel("openai/gpt-4o-mini");
    setApiBase("https://");
    setApiKeyEnv("OPENAI_KEY");
  }

  const yamlPreview = useMemo(() => {
    return endpoints
      .map(
        (ep) => `  - model_name: ${ep.alias}\n    litellm_params:\n      model: ${ep.model}\n      api_base: ${ep.apiBase}\n      api_key: "\${${ep.apiKeyEnv}}"`,
      )
      .join("\n");
  }, [endpoints]);

  return (
    <section className="rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
      <div className="mb-4 flex items-center justify-between gap-2">
        <h2 className="text-lg font-medium">Multi-CustomOpenAI Bastelkasten</h2>
        <span className="text-xs text-slate-400">GUI-Prototyp · lokal</span>
      </div>

      <form onSubmit={addEndpoint} className="grid gap-3 rounded-xl border border-slate-800 bg-slate-950/70 p-4 md:grid-cols-2">
        <label className="space-y-1 text-sm">
          <span className="text-slate-300">Alias</span>
          <input
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
            className="w-full rounded-md border border-slate-700 bg-slate-900 px-3 py-2 text-sm"
            placeholder="team-assistant"
          />
        </label>

        <label className="space-y-1 text-sm">
          <span className="text-slate-300">Model</span>
          <input
            value={model}
            onChange={(e) => setModel(e.target.value)}
            className="w-full rounded-md border border-slate-700 bg-slate-900 px-3 py-2 text-sm"
            placeholder="openai/gpt-4o-mini"
          />
        </label>

        <label className="space-y-1 text-sm">
          <span className="text-slate-300">API Base URL</span>
          <input
            value={apiBase}
            onChange={(e) => setApiBase(e.target.value)}
            className="w-full rounded-md border border-slate-700 bg-slate-900 px-3 py-2 text-sm"
            placeholder="https://my-endpoint/v1"
          />
        </label>

        <label className="space-y-1 text-sm">
          <span className="text-slate-300">ENV Key Name</span>
          <input
            value={apiKeyEnv}
            onChange={(e) => setApiKeyEnv(e.target.value)}
            className="w-full rounded-md border border-slate-700 bg-slate-900 px-3 py-2 text-sm"
            placeholder="OPENAI_KEY"
          />
        </label>

        <button
          type="submit"
          className="md:col-span-2 rounded-md border border-cyan-700 bg-cyan-900/40 px-3 py-2 text-sm font-medium text-cyan-100"
        >
          Endpoint hinzufügen
        </button>
      </form>

      <div className="mt-4 grid gap-4 lg:grid-cols-2">
        <article className="rounded-xl border border-slate-800 bg-slate-950/70 p-4">
          <h3 className="mb-2 text-sm font-medium text-slate-200">Aktive Custom Endpoints</h3>
          <ul className="space-y-2 text-sm">
            {endpoints.map((ep) => (
              <li key={ep.id} className="rounded-md border border-slate-800 bg-slate-900/70 p-2">
                <p className="font-medium text-slate-100">{ep.alias}</p>
                <p className="text-slate-300">{ep.model}</p>
                <p className="text-xs text-slate-400">{ep.apiBase}</p>
              </li>
            ))}
          </ul>
        </article>

        <article className="rounded-xl border border-slate-800 bg-slate-950/70 p-4">
          <h3 className="mb-2 text-sm font-medium text-slate-200">config.yaml Vorschau</h3>
          <pre className="max-h-72 overflow-auto rounded-md border border-slate-800 bg-black/50 p-3 text-xs text-cyan-100">
{`model_list:\n${yamlPreview}`}
          </pre>
        </article>
      </div>
    </section>
  );
}
