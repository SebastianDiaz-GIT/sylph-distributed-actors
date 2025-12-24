# SYLPH — Sistema de Actores (v0.1)

Resumen
-------
Sylph es un runtime de actores local-first para Java, orientado a facilitar la creación de aplicaciones concurrentes usando el modelo de actores.
Esta versión (v0.1) provee:

- API pública mínima y tipada: `Actor`, `ActorRef<M>`, `ActorContext<M>`, `ActorSystem`.
- Implementación local por defecto (`DefaultActorSystem`).
- Mailboxes: FIFO y Priority.
- Lifecycle básico de actores (CREATED → RUNNING → STOPPING → STOPPED).
- Métricas por actor y exportador HTTP simple (`/metrics`).
- Logger minimal integrado (stdout) para eventos del runtime.
- API fluent (builder) para `spawn(...)` con opciones encadenadas.
- Ejemplos y demos ejecutables.

Principios
----------
- Un actor procesa un mensaje a la vez.
- Mensajes inmutables (usa `record` o tipos inmutables para tus mensajes).
- La API pública no expone hilos, executores ni detalles de concurrencia.
- Virtual Threads (Project Loom) se usan internamente en el runtime.

Novedades en esta versión
-------------------------
1. Lifecycle
   - Estados: `CREATED -> RUNNING -> STOPPING -> STOPPED`.
   - `stop()` drena la mailbox (procesa mensajes encolados) antes de marcar `STOPPED`.
   - Mensajes enviados durante `STOPPING`/`STOPPED` son rechazados (conteo en métricas).

2. Métricas y observabilidad
   - `ActorMetrics` registra: mensajes procesados, mensajes rechazados y transiciones de estado.
   - `ActorSystemImpl` expone utilidades: `getMetrics(name)`, `getAllMetrics()` y `logAllMetrics()`.
   - `MetricsHttpExporter` (Demo): exporta métricas en texto plano en `GET /metrics` (puerto configurable, demo por defecto en 8000).

3. Logging
   - `sylph.util.logging.Logger` es un logger muy simple sin dependencias externas (stdout).
   - El runtime usa el logger para eventos importantes (spawn, stop, errores en `receive`, transiciones de lifecycle).
   - Recomendación: en proyectos productivos cambiar a SLF4J/Logback más adelante.

4. API Fluent (Builder) para spawn
   - Nueva forma de crear actores con opciones encadenadas:

```java
ActorRef<MyMessage> ref = system.spawn(MyActor::new)
    .withName("printer")
    .withMailbox(MailboxType.PRIORITY)
    .withSupervision(Supervision.RESTART) // por ahora placeholder
    .spawn();
```

   - `withSupervision(...)` queda como placeholder: la opción se guarda en el builder y puede habilitarse más tarde con la política deseada (RESTART, STOP, NONE).

Contenido del repositorio
-------------------------
- `src/main/java/sylph/api` - API pública (interfaces y fábrica).
- `src/main/java/sylph/actors` - runtime interno (implementación y referencias internas).
- `src/main/java/sylph/api/internal` - adaptadores entre la API pública y el runtime.
- `src/main/java/sylph/mailbox` - mailboxes (FIFO, PRIORITY).
- `src/main/java/sylph/examples` - demos ejecutables.
- `src/main/java/sylph/util/metrics` - métricas y exportador HTTP.
- `src/main/java/sylph/util/logging` - logger minimal.

Requisitos
----------
- JDK 21 (Project Loom) — necesario para hilos virtuales (`Thread.ofVirtual()`)

Si no tienes JDK 21, instala uno (Adoptium Temurin, Oracle JDK, etc.) y asegúrate de que `java -version` muestre `21.x`.

Rápido inicio (Windows PowerShell)
----------------------------------
1) Preparar el entorno (ajusta la ruta a tu instalación de JDK 21):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

2) Compilar el proyecto con Gradle:

```powershell
.\gradlew.bat clean build --console=plain
```

3) Ejecutar demos (ejemplos):

```powershell
# Demo principal
java -cp "build\classes\java\main" sylph.examples.PrinterDemo

# Demo que espera ENTER antes de cerrar
java -cp "build\classes\java\main" sylph.examples.PrinterDemoKeepAlive

# Demo con shutdown automático
java -cp "build\classes\java\main" sylph.examples.PrinterDemoAutoShutdown

# Demo con exportador de métricas (puerto 8000)
java -cp "build\classes\java\main" sylph.examples.DemoWithMetrics
# Luego en otra shell:
Invoke-RestMethod 'http://localhost:8000/metrics'
```

Ejemplo mínimo (uso recomendado)
-------------------------------
```java
try (ActorSystem system = ActorSystems.create()) {
    ActorRef<String> printer = system.spawn("printer", PrinterActor::new);
    printer.tell("hola");
}
```

Ejemplo usando la API fluent (builder)
-------------------------------------
```java
try (ActorSystem system = ActorSystems.create()) {
    ActorRef<String> printer = system.spawn(PrinterActor::new)
        .withName("printer")
        .withMailbox(MailboxType.FIFO)
        .withSupervision(Supervision.NONE)
        .spawn();
    printer.tell("hola");
}
```

Métricas y observabilidad
-------------------------
- El exportador HTTP incluido es intencionalmente mínimo y no está pensado para producción. Conviene migrar a Micrometer + Prometheus en versiones futuras.
- `ActorSystemImpl.logAllMetrics()` permite volcar en logs las métricas actuales.

Buenas prácticas
----------------
- Mantén los mensajes inmutables (usar `record`).
- Evita exponer hilos y ejecutores en la API pública.
- Usa `tell` con mensajes semánticos en vez de strings crudos para gestionar comportamiento de actors.
- Si necesitas logging serio, integra SLF4J/Logback en tu proyecto y reemplaza el logger minimal.

Siguientes pasos recomendados (v0.2)
-----------------------------------
- Implementar supervisión activa (RESTART / STOP) en el runtime.
- Bounded mailboxes y backpressure.
- `shutdownGracefully(Duration timeout)` en `ActorSystem`.
- Migrar métricas a formato Prometheus y/o integrar Micrometer.
- Tests unitarios para lifecycle, builder y métricas.

Contacto / contribuciones
-------------------------
Abre issues o PR en el repositorio con propuestas. Antes de cambios grandes, propone una RFC en un issue para discusión.

Licencia
--------
- Por definir 


---
*README actualizado para incluir lifecycle, builder API, métricas y logging (v0.2.0)*
