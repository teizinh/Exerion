# 🦀 Exerion Native Engine — Plano de Arquitetura & Implementação em Rust (`crates/exerion-grid`)

Este documento estabelece o roadmap técnico e a arquitetura para a integração de um motor nativo em **Rust** no mod **Exerion** para resolver o gargalo de tick e grafo elétrico/mecânico.

---

## 🎯 1. O Problema a Resolver (Java vs Rust)

* **Gargalo no Java (`CableBlockEntity.java`):**
  * Cada bloco de cabo efetua `tick()` 20 vezes por segundo.
  * Em cada tick, consulta as 6 direções vizinhas (`level.getBlockEntity(pos.relative(dir))`).
  * Numa base com 200 cabos: $200 \times 6 = 1.200$ lookups por tick $\rightarrow$ **24.000 lookups/s na thread principal**.
  * A energia propaga-se de forma iterativa/lenta entre vizinhos (`(energy - neighbor.energy) / 2`).

* **A Solução com Rust (`exerion-grid`):**
  * **Cabos sem tick no Java (`tick = null`):** Redução de 100% da carga de polling na thread do servidor.
  * **Grafo Contíguo em Rust:** O Rust mantém a topologia da rede através de um **Disjoint-Set / Union-Find** e listas de adjacência contíguas na memória cache.
  * **Distribuição Instantânea ($O(1)$ amortizado):** Num único ciclo de CPU (menos de $0.005\text{ ms}$), o solver em Rust equilibra fontes de energia e consumidores sem gerar alocações na Heap do Java (zero GC pressure).

---

## 🏗️ 2. Estrutura do Workspace

```text
Exerion/
├── Cargo.toml                      # Workspace Cargo raiz
├── crates/
│   └── exerion-grid/               # Crate nativa em Rust
│       ├── Cargo.toml              # crate-type = ["cdylib", "rlib"]
│       ├── src/
│       │   ├── lib.rs              # JNI exports (C-ABI estável)
│       │   ├── grid.rs             # Estrutura do Grafo & Union-Find
│       │   ├── solver.rs           # Distribuição instantânea de energia & perda de carga
│       │   └── tests.rs            # Benchmarks e testes unitários
├── src/main/java/net/exerion/
│   └── energy/
│       ├── RustGridEngine.java     # Bridge JNI & Carregador de binários (.so/.dll)
│       └── JavaGridFallback.java   # Fallback automático caso a lib nativa não exista
└── src/main/resources/natives/     # Binários embutidos no .jar
    ├── libexerion_grid.so          # Linux x86_64
    └── exerion_grid.dll            # Windows x86_64
```

---

## 📋 3. Fases de Execução

1. **Fase 1: Motor Rust (`crates/exerion-grid`):**
   * Configuração de compilação com `mold` e `release` agressivo (`lto = true`, `opt-level = 3`, `codegen-units = 1`).
   * Implementação de nós (`GridNode`), geradores (`PowerSource`), consumidores (`PowerSink`) e cabos (`CableConnection`).
   * Bindings JNI exportando: `create_grid`, `add_cable`, `remove_cable`, `connect_device`, `tick_distribution`.
2. **Fase 2: Conector Java & Fallback:**
   * `RustGridEngine.java` com carregamento automático via `System.loadLibrary` a partir do `natives/` temporário.
   * `JavaGridFallback.java` garantindo 100% de estabilidade mesmo se o SO bloquear bibliotecas nativas.
3. **Fase 3: Integração no Jogo:**
   * Atualização do `CableBlock` e `BatBoxBlockEntity` para despacharem o fluxo pelo motor nativo.
4. **Fase 4: Cross-compilação Windows / Linux:**
   * Geração do `.so` e `.dll` para incluir nas releases oficiais.
