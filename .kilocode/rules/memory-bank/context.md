# Active Context: Catalon-Guard Dashboard + Windows-first DX

## Current State

**Template Status**: ✅ Dashboard mockup exists, Windows-native setup is available, and a portable Windows bundle build path is documented.

The project now includes dedicated Windows scripts for dependency installation and proxy startup, plus cross-platform `.env` handling so users can run Catalon-Guard directly on Windows.

## Recently Completed

- [x] Added Windows environment template file: `catalon-guard/.env.template`
- [x] Added Windows setup script: `catalon-guard/setup_windows.ps1`
- [x] Added Windows run script: `catalon-guard/run_proxy_windows.ps1`
- [x] Rewrote `catalon-guard/README.md` with Windows-first Quick Start (no WSL required)
- [x] Updated Linux setup script to use `.env.template` + `python3 -m pip`
- [x] Updated Linux run script to auto-load `.env` with `KEY=VALUE` compatibility
- [x] Added portable Windows bundle builder: `catalon-guard/portable/build_portable.ps1`
- [x] Documented portable distribution flow in README (no global dependencies on target machine)
- [x] Hardened portable build script with Python 3.11/3.12 selection and binary-wheel install strategy for `orjson`
- [x] Fixed portable launcher entrypoint to call `runtime\Scripts\litellm.exe` (not `python -m litellm`)
- [x] Added explicit `prometheus-client` installation in setup + portable build scripts to prevent startup ModuleNotFoundError

## Current Structure

| File/Directory | Purpose | Status |
|----------------|---------|--------|
| `src/app/page.tsx` | Pretext-inspired Catalon-Guard dashboard GUI | ✅ Implemented |
| `catalon-guard/setup_windows.ps1` | Windows setup flow | ✅ Implemented |
| `catalon-guard/run_proxy_windows.ps1` | Windows proxy startup flow | ✅ Implemented |
| `catalon-guard/setup.sh` | Linux/macOS setup flow | ✅ Updated |
| `catalon-guard/run_proxy.sh` | Linux/macOS startup flow | ✅ Updated |
| `catalon-guard/README.md` | Windows-first usage docs | ✅ Updated |

## Current Focus

Next likely steps for this project:

1. Connect dashboard cards and tables to real proxy/runtime metrics
2. Add direct model CRUD actions in UI (replace manual CLI loop)
3. Add trends/charts for spend and latency over time

## Session History

| Date | Changes |
|------|---------|
| Initial | Template created with base setup |
| 2026-04-03 | Implemented initial Catalon-Guard dashboard UI mockup and dark global styling updates. |
| 2026-04-03 | Refined dashboard into a Pretext-inspired UI pass with calmer typography and improved scanability. |
| 2026-04-04 | Added native Windows setup/start scripts and rewrote docs for Windows-first usage without WSL. |
| 2026-04-04 | Added portable Windows bundle build script (`portable/build_portable.ps1`) and README instructions for no-global-dependency distribution. |
| 2026-04-04 | Fixed portable build reliability for `orjson` wheel build failures by enforcing Python 3.11/3.12 and binary wheel installation. |
| 2026-04-04 | Fixed portable startup error (`No module named litellm.__main__`) by switching launcher to `runtime\Scripts\litellm.exe`. |
| 2026-04-04 | Fixed startup error `No module named prometheus_client` by explicitly installing `prometheus-client` in setup and portable build flows. |
