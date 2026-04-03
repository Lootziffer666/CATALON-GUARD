const kpis = [
  { label: "Daily Spend", value: "$1.84", detail: "37% von $5.00 Limit" },
  { label: "Requests/min", value: "27", detail: "Peak 41 @ 14:03 UTC" },
  { label: "Blocked", value: "3", detail: "Token cap > 8,192" },
  { label: "Models", value: "10", detail: "Vertex + OpenRouter" },
];

const models = [
  ["gpt-4o", "vertex/gemini-1.5-pro-002", "healthy", "1.3s"],
  ["gpt-4.5", "vertex/gemini-2.0-flash", "healthy", "0.8s"],
  ["gpt-4o-mini", "vertex/gemini-1.5-flash-002", "healthy", "0.6s"],
  ["refinery", "openrouter/nomic-ai/mimo-v2-pro", "watch", "2.1s"],
  ["qwen-coder", "openrouter/qwen-2.5-coder-32b", "healthy", "1.5s"],
] as const;

const timeline = [
  "14:03 — rate spike, soft limit warning emitted",
  "14:04 — refinery latency > 2s, routed fallback remains stable",
  "14:06 — 3 oversized requests rejected (HTTP 429)",
  "14:07 — budget trend normalisiert sich",
];

export default function Home() {
  return (
    <main className="min-h-screen bg-[#030712] text-slate-100">
      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-10">
        <section className="mb-6 grid gap-4 rounded-3xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur sm:grid-cols-[1.3fr_1fr]">
          <div>
            <p className="text-xs tracking-[0.28em] text-cyan-300/90">CATALON-GUARD / CONTROL SURFACE</p>
            <h1 className="mt-3 text-3xl font-semibold leading-tight sm:text-4xl">
              Pretext-inspirierter Ops-Look:
              <span className="mt-1 block text-slate-300">ruhige Typografie, klare Signale, hoher Fokus</span>
            </h1>
            <p className="mt-4 max-w-2xl text-sm text-slate-300">
              Fokus auf Lesbarkeit und Informationshierarchie statt „lauter“ Admin-UI. Die Oberfläche priorisiert Routing-Status,
              Budgetdruck und konkrete Operator-Aktionen.
            </p>
          </div>
          <div className="rounded-2xl border border-slate-700/70 bg-slate-950/70 p-4">
            <p className="text-xs uppercase tracking-widest text-slate-400">System state</p>
            <div className="mt-3 space-y-2 text-sm text-slate-300">
              <p>Proxy: <span className="text-emerald-300">online</span></p>
              <p>Mode: <span className="text-cyan-300">budget-guarded</span></p>
              <p>Policy: <span className="text-amber-300">80% warn / 100% block</span></p>
              <p>Master key: <span className="text-slate-200">loaded</span></p>
            </div>
          </div>
        </section>

        <section className="mb-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {kpis.map((kpi) => (
            <article key={kpi.label} className="rounded-2xl border border-slate-800 bg-slate-900/50 p-4">
              <p className="text-xs uppercase tracking-widest text-slate-400">{kpi.label}</p>
              <p className="mt-2 text-3xl font-semibold text-slate-100">{kpi.value}</p>
              <p className="mt-1 text-sm text-slate-300">{kpi.detail}</p>
            </article>
          ))}
        </section>

        <section className="grid gap-6 lg:grid-cols-[1.4fr_1fr]">
          <article className="rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
            <div className="mb-3 flex items-end justify-between">
              <h2 className="text-lg font-medium">Model Routing Matrix</h2>
              <span className="text-xs text-slate-400">snapshot / mocked</span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full min-w-[560px] text-left text-sm">
                <thead className="text-xs uppercase tracking-widest text-slate-400">
                  <tr className="border-b border-slate-800">
                    <th className="pb-2">Alias</th>
                    <th className="pb-2">Provider Route</th>
                    <th className="pb-2">Status</th>
                    <th className="pb-2">P95 Latency</th>
                  </tr>
                </thead>
                <tbody>
                  {models.map(([alias, route, status, latency]) => (
                    <tr key={alias} className="border-b border-slate-800/80">
                      <td className="py-3 font-medium">{alias}</td>
                      <td className="py-3 text-slate-300">{route}</td>
                      <td className="py-3">
                        <span className="rounded-md border border-slate-700 bg-slate-950 px-2 py-1 text-xs uppercase tracking-wide">
                          {status}
                        </span>
                      </td>
                      <td className="py-3 text-slate-300">{latency}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </article>

          <article className="rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
            <h2 className="text-lg font-medium">Operator Timeline</h2>
            <ul className="mt-4 space-y-2 text-sm text-slate-300">
              {timeline.map((entry) => (
                <li key={entry} className="rounded-lg border border-slate-800 bg-slate-950/80 px-3 py-2">
                  {entry}
                </li>
              ))}
            </ul>
            <div className="mt-5 rounded-xl border border-cyan-800/50 bg-cyan-950/30 p-3 text-xs text-cyan-100">
              Next step: echte Daten via LiteLLM Admin/API einbinden und Karten in live telemetry umschalten.
            </div>
          </article>
        </section>
      </div>
    </main>
  );
}
