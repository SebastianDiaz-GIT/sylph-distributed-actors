# Changelog

## 0.2.2 - (unreleased)
### Added
- Mejora en `spawnChild`: soporte completo para jerarquías recursivas de actores (padre → hijos → nietos ...). Cada actor puede crear 0..N hijos y los hijos pueden crear a su vez sus propios hijos.
- Supervisión jerárquica: los padres pueden supervisar a sus hijos y aplicar políticas (RESTART/STOP) a nivel de hijo. La política del padre puede propagarse o aplicarse de forma individual a cada hijo.
- `ActorContext.spawnChild(...)` y builder `.spawnChild(...)` en el API fluent para crear hijos con nombre, mailbox y política de supervisión.
- Propagación de `stop()`: detener un actor ahora opcionalmente detiene recursivamente a todos sus hijos (configurable desde el builder o `ActorContext`).
- Tests: `ChildSpawnTest` ampliado para cubrir creación recursiva, parada recursiva y supervisión de hijos; nuevos tests unitarios para `spawnChild` depth/width y comportamiento de parada.
- Documentación: ejemplos y guías en `README.md` y en `.github/instructions/test.instructions.md` para probar jerarquías de actores y medir su impacto en rendimiento.

### Changed
- Ajustes menores en métricas para incorporar conteo de hijos por actor y restarts por hijo.

### Fixed
- Evitado loop infinito en creación de hijos debida a usos accidentales de factories recursivas: ahora se detectan y limitan recursiones excesivas con un umbral configurable en runtime.


## 0.2.1 - (unreleased)
### Added
- Supervisión básica de actores: políticas RESTART y STOP y mecanismo de rechazo (REJECT) para mensajes que no deben procesarse tras STOP.
- Tests de supervisión y demo (`SupervisionAndLifecycleTest`, `SupervisionE2ERestartTest`).
- `TestProbe` y utilidades de testing para verificar mensajes, orden y tolerancia a fallos en tests unitarios.
- Archivo de instrucciones para pruebas: `.github/instructions/test.instructions.md` (benchmarks, throughput, tolerancia a fallos, validación determinista).
- Actualizaciones en el `README.md` con ejemplos de uso y guía rápida de supervisión y lifecycle.

### Fixed
- `stop()` ahora drena la mailbox y rechaza nuevos mensajes (garantía: STOPPING → STOPPED).
- Correcciones menores en el flujo de lifecycle para asegurar transiciones determinísticas (CREATED → RUNNING → STOPPING → STOPPED).

## 0.2.0 - (unreleased)
### Added
- Lifecycle formal para actores (CREATED → RUNNING → STOPPING → STOPPED).
- Drain de mailbox en `stop()`.
- Métricas por actor (`ActorMetrics`) y `MetricsHttpExporter` (demo).
- Logger minimal integrado.
- API fluent (builder) para `spawn(...)` con `.withName()/.withMailbox()/.withSupervision()`.

### Breaking changes
- La llamada `system.spawn(MyActor::new)` ahora inicia un builder; el equivalente directo es:
  `system.spawn(MyActor::new).spawn()`
  Documenta y actualiza el código que dependía de la antigua forma.

### Notes
- Versión pre‑1.0, API todavía experimental.