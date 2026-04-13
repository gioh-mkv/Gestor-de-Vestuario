# ClosetFlow

> Aplicação desktop em Java para organizar e acompanhar o seu guarda-roupa de forma inteligente.

---

## 📋 Sobre o Projeto

O **ClosetFlor** é um sistema de gestão de vestuário pessoal com interface gráfica (Swing), voltado para quem deseja ter controle completo sobre suas roupas e acessórios. Com ele é possível cadastrar peças, montar looks, registrar lavagens, controlar empréstimos e visualizar estatísticas de uso — tudo salvo localmente em JSON.

### Funcionalidades

| Módulo | Descrição |
|---|---|
| **Itens** | Cadastro de camisas, calças, relógios e roupas íntimas com cor, tamanho, loja e estado de conservação |
| **Looks** | Criação de combinações de peças com registro de data, período e ocasião de uso |
| **Empréstimos** | Controle de itens emprestados a outras pessoas, com rastreamento de dias |
| **Lavagens** | Registro de lavagens em lote com histórico e observações |
| **Estatísticas** | Painel com os itens mais/menos usados, looks favoritos e itens mais lavados |

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 11** ou superior instalado ([Baixar Adoptium JRE](https://adoptium.net))

### Windows

1. Faça o download dos arquivos `ClosetFlow.jar` e `ClosetFlow.bat`
2. Coloque ambos na mesma pasta
3. Dê duplo clique em `ClosetFlow.bat`

> Alternativamente, execute diretamente via terminal:
> ```cmd
> java -jar ClosetFlow.jar
> ```

### Linux / macOS

1. Faça o download dos arquivos `ClosetFlow.jar` e `ClosetFlow.sh`
2. Coloque ambos na mesma pasta
3. Torne o script executável e execute:

```bash
chmod +x ClosetFlow.sh
./ClosetFlow.sh
```

> Alternativamente:
> ```bash
> java -jar ClosetFlow.jar
> ```

---

## 🛠️ Como Compilar (para desenvolvedores)

### Requisitos

- JDK 11+
- `gson-2.10.1.jar` (incluso em `classpath/`)

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/gioh-mkv/Gestor-de-Vestuario.git
cd Gestor-de-Vestuario

# 2. Compile os fontes
mkdir -p build
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "classpath/gson-2.10.1.jar" -d build @sources.txt

# 3. Extraia as dependências no diretório de build
cd build && jar xf ../classpath/gson-2.10.1.jar && cd ..

# 4. Gere o JAR executável
echo "Main-Class: gui.MainFrame" > manifest.mf
jar cfm gvp.jar manifest.mf -C build .

# 5. Execute
java -jar ClosetFlow.jar
```

---

## 📁 Estrutura do Projeto

```
Gestor-de-Vestuario/
├── src/
│   ├── gui/                  # Painéis e janela principal (Swing)
│   │   ├── MainFrame.java
│   │   ├── ItemPanel.java
│   │   ├── LookPanel.java
│   │   ├── EmprestimoPanel.java
│   │   ├── LavagemPanel.java
│   │   └── EstatisticaPanel.java
│   ├── model/                # Entidades do domínio
│   │   ├── Item.java           (abstrata)
│   │   ├── ItemEmprestavel.java (abstrata — base para itens emprestáveis)
│   │   ├── ItemLavavel.java     (abstrata — base para itens laváveis)
│   │   ├── Camisa.java
│   │   ├── Calca.java
│   │   ├── Relogio.java
│   │   ├── RoupaIntima.java
│   │   ├── Look.java
│   │   └── Lavagem.java
│   ├── interfaces/           # Contratos de comportamento
│   │   ├── IEmprestavel.java
│   │   └── ILavavel.java
│   └── service/              # Lógica de negócio e persistência
│       ├── GestorVestuario.java
│       └── PersistenciaService.java
├── classpath/
│   └── gson-2.10.1.jar       # Dependência para serialização JSON
├── ClosetFlow.jar                   # Executável (gerado após compilação)
├── ClosetFlow.sh                    # Launcher Linux/macOS
├── ClosetFlow.bat                   # Launcher Windows
└── README.md
```

### Dados Salvos

Os dados são persistidos automaticamente na pasta `dados/` ao lado do `ClosetFlow.jar`:

```
dados/
├── itens.json
├── looks.json
└── lavagens.json
```

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas com separação clara entre modelo, serviço e interface:

- **Herança** — `ItemEmprestavel` e `ItemLavavel` eliminam duplicação de código entre `Camisa`, `Calça` e `Relógio`
- **Interfaces** — `IEmprestavel` e `ILavavel` definem contratos independentes da hierarquia de classes
- **Polimorfismo** — `GestorVestuario` opera sobre `Item` e usa `instanceof` apenas onde necessário
- **Persistência** — `PersistenciaService` com serialização polimórfica via Gson, preservando o tipo concreto de cada item no JSON

---

## 📦 Dependências

| Biblioteca | Versão | Uso |
|---|---|---|
| [Gson](https://github.com/google/gson) | 2.10.1 | Serialização/desserialização JSON |

---

## 📄 Licença

Este projeto é de uso pessoal e educacional. Distribuído sem garantias.
