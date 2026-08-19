@echo off
setlocal EnableExtensions

cd /d "%~dp0.."

echo.
echo ============================================
echo  ConfiaPix - Seed de dados de demonstracao
echo ============================================
echo.

where docker >nul 2>&1
if errorlevel 1 (
  echo [ERRO] Docker nao encontrado. Instale o Docker Desktop e tente novamente.
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0seed-demo-data.ps1"
if errorlevel 1 (
  echo.
  echo [ERRO] Falha ao executar seed.
  exit /b 1
)

echo.
pause
endlocal
