# Changelog

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
- API fluent builder para `spawn(...)` con `.withName()/.withMailbox()/.withSupervision()`.

### Breaking changes
- La llamada `system.spawn(MyActor::new)` ahora inicia un builder; el equivalente directo es:
  `system.spawn(MyActor::new).spawn()`
  Documenta y actualiza el código que dependía de la antigua forma.

### Notes
- Versión pre‑1.0, API todavía experimental.