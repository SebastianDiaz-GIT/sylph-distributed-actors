# Changelog

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