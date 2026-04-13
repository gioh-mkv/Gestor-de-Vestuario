#!/bin/bash
# ClosetFlow — Gestor de Vestuário Pessoal
# Launcher para Linux/macOS

DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$DIR/gvp.jar"

if [ ! -f "$JAR" ]; then
    echo "Erro: gvp.jar não encontrado em $DIR"
    exit 1
fi

# Verifica Java (requer >= 11)
if ! command -v java &> /dev/null; then
    echo "Java não encontrado. Instale o JRE 11 ou superior."
    echo "  Ubuntu/Debian : sudo apt install default-jre"
    echo "  Fedora/RHEL   : sudo dnf install java-21-openjdk"
    echo "  macOS         : brew install openjdk"
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_VER" -lt 11 ] 2>/dev/null; then
    echo "Requer Java 11+. Versão encontrada: $JAVA_VER"
    exit 1
fi

exec java -jar "$JAR" "$@"
