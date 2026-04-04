import MultiCustomOpenAIWorkbench from "@/components/MultiCustomOpenAIWorkbench";

const kpis = [
  { label: "Daily Spend", value: "$1.84", detail: "37% von $5.00 Limit" },
  { label: "Requests/min", value: "27", detail: "Peak 41 @ 14:03 UTC" },
  { label: "Blocked", value: "3", detail: "Token cap > 8,192" },
  { label: "Models", value: "10", detail: "Vertex + OpenRouter" },
];

export default function Home() {
  return (
    <main className="min-h-screen bg-[#030712] text-slate-100">
      <div className="mx-auto max-w-7xl space-y-6 px-4 py-8 sm:px-6 lg:px-10">
        <section className="grid gap-4 rounded-3xl border border-slate-800/80 bg-slate-900/60 p-6 backdrop-blur sm:grid-cols-[1.3fr_1fr]">
          <div>
            <p className="text-xs tracking-[0.28em] text-cyan-300/90">CATALON-GUARD / CONTROL SURFACE</p>
            <h1 className="mt-3 text-3xl font-semibold leading-tight sm:text-4xl">Runtime GUI + Multi-CustomOpenAI Toolkit</h1>
            <p className="mt-4 max-w-2xl text-sm text-slate-300">
              Nicht nur Mockup: unten findest du jetzt den Bastelkasten, um mehrere Custom-OpenAI-Endpunkte zusammenzustellen und direkt als
              `config.yaml`-Block vorzubereiten.
            </p>
          </div>
          <div className="rounded-2xl border border-slate-700/70 bg-slate-950/70 p-4 text-sm text-slate-300">
            <p className="text-xs uppercase tracking-widest text-slate-400">System state</p>
            <p className="mt-3">Proxy: <span className="text-emerald-300">online</span></p>
            <p>Mode: <span className="text-cyan-300">budget-guarded</span></p>
            <p>Policy: <span className="text-amber-300">80% warn / 100% block</span></p>
          </div>
        </section>

        <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {kpis.map((kpi) => (
            <article key={kpi.label} className="rounded-2xl border border-slate-800 bg-slate-900/50 p-4">
              <p className="text-xs uppercase tracking-widest text-slate-400">{kpi.label}</p>
              <p className="mt-2 text-3xl font-semibold text-slate-100">{kpi.value}</p>
              <p className="mt-1 text-sm text-slate-300">{kpi.detail}</p>
            </article>
          ))}
        </section>

        <MultiCustomOpenAIWorkbench />
      </div>
    </main>
  );
}
