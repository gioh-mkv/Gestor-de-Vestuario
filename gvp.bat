@echo off
:: GVP — Gestor de Vestuário Pessoal
:: Launcher para Windows

setlocal

set "DIR=%~dp0"
set "JAR=%DIR%gvp.jar"

if not exist "%JAR%" (
    echo Erro: gvp.jar nao encontrado em %DIR%
    pause
    exit /b 1
)

:: Verifica se Java esta instalado
where java >nul 2>nul
if errorlevel 1 (
    echo Java nao encontrado.
    echo Baixe e instale o JRE 11 ou superior em: https://adoptium.net
    pause
    exit /b 1
)

:: Inicia o aplicativo (sem janela de console)
start "" javaw -jar "%JAR%"
endlocal
