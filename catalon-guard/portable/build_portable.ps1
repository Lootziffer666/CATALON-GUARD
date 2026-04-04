# Build a portable Windows bundle for Catalon-Guard (no WSL, no global Python needed on target machine)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$distRoot = Join-Path $root "dist"
$bundle = Join-Path $distRoot "CatalonGuardPortable"
$runtime = Join-Path $bundle "runtime"

Write-Host "=== Building Catalon-Guard Portable Bundle ===" -ForegroundColor Green

if (Test-Path $bundle) {
    Remove-Item $bundle -Recurse -Force
}

New-Item -ItemType Directory -Path $bundle | Out-Null

$pythonCmd = $null
if (Get-Command py -ErrorAction SilentlyContinue) {
    $pythonCmd = "py"
} elseif (Get-Command python -ErrorAction SilentlyContinue) {
    $pythonCmd = "python"
} else {
    throw "Python is required on the build machine."
}

Write-Host "Using Python command: $pythonCmd"
& $pythonCmd -m venv $runtime

$pyExe = Join-Path $runtime "Scripts\python.exe"
& $pyExe -m pip install --upgrade pip
& $pyExe -m pip install "litellm[proxy]" requests

Copy-Item (Join-Path $root "config.yaml") (Join-Path $bundle "config.yaml")
Copy-Item (Join-Path $root ".env.template") (Join-Path $bundle ".env.template")
Copy-Item (Join-Path $root "model_manager.py") (Join-Path $bundle "model_manager.py")
Copy-Item (Join-Path $root "guard_stats.py") (Join-Path $bundle "guard_stats.py")

$startBat = @"
@echo off
setlocal
cd /d %~dp0
if not exist .env (
  copy .env.template .env >nul
  echo Created .env from .env.template
)
for /f "usebackq tokens=* delims=" %%A in (".env") do (
  set "line=%%A"
  call :setLine
)
if "%GOOGLE_PROJECT_ID%"=="" (
  echo ERROR: GOOGLE_PROJECT_ID missing in .env
  pause
  exit /b 1
)
if "%GOOGLE_LOCATION%"=="" set GOOGLE_LOCATION=us-central1
if "%LITELLM_MASTER_KEY%"=="" set LITELLM_MASTER_KEY=sk-catalon-safe-key

echo Starting LiteLLM Proxy...
echo Dashboard: http://localhost:4000/ui
echo API:       http://localhost:4000/v1
runtime\Scripts\python.exe -m litellm --config config.yaml --port 4000 --debug
exit /b %errorlevel%

:setLine
if "%line%"=="" goto :eof
if "%line:~0,1%"=="#" goto :eof
for /f "tokens=1,* delims==" %%K in ("%line%") do (
  set "key=%%K"
  set "val=%%L"
)
if defined key set "%key%=%val%"
goto :eof
"@

Set-Content -Path (Join-Path $bundle "start_portable.bat") -Value $startBat -Encoding ASCII

$readme = @"
Catalon-Guard Portable (Windows)
================================

1) start_portable.bat starten
2) Beim ersten Start wird .env aus .env.template erzeugt
3) .env ausfüllen (API Keys)
4) start_portable.bat erneut starten

Hinweis:
- Dieses Bundle enthält eine eigene Python-Runtime + Abhängigkeiten lokal im Ordner ./runtime.
- Auf dem Zielsystem ist keine globale Python-Installation nötig.
"@
Set-Content -Path (Join-Path $bundle "README_PORTABLE.txt") -Value $readme -Encoding UTF8

Write-Host "Portable bundle created: $bundle" -ForegroundColor Green
Write-Host "Zip for distribution:" -ForegroundColor Cyan
Write-Host "  Compress-Archive -Path '$bundle\\*' -DestinationPath '$distRoot\\CatalonGuardPortable.zip' -Force"
