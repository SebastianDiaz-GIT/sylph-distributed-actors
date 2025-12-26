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
1. Supervisión básica
   - Políticas soportadas: `RESTART`, `STOP`, `NONE`.
   - Al ocurrir una excepción en `receive()`, el runtime aplica la política configurada para ese actor: reiniciar la instancia (RESTART), detener el actor y drenar su mailbox (STOP) o no hacer nada especial (NONE).
   - `REJECT` (mecanismo de rechazo): después de `STOPPING`, nuevos mensajes son rechazados y contabilizados en métricas.

2. Spawn de hijos (spawnChild)
   - Un actor puede crear 0..N hijos mediante API interna `ActorContext.spawnChild(...)` o usando el builder desde el `ActorSystem`.
   - Cada hijo conoce a su padre y la jerarquía es recursiva: un hijo puede tener a su vez sus propios hijos.
   - Los padres pueden supervisar a sus hijos y tomar acciones (por ejemplo: detener a todos los hijos al detenerse).
   - Ejemplo conceptual:

```java
ActorRef<Cmd> parent = system.spawn(ParentActor::new)
    .withName("parent")
    .spawn();

// desde ParentActor.receive(ctx):
ActorRef<Task> child = ctx.spawnChild(TaskActor::new, "worker-1");
```

3. API Fluent (Builder) para spawn
   - Crear actores con opciones encadenadas:

```java
ActorRef<MyMessage> ref = system.spawn(MyActor::new)
    .withName("printer")
    .withMailbox(MailboxType.PRIORITY)
    .withSupervision(Supervision.RESTART)
    .spawn();
```

4. Testing y utilidades
   - `TestProbe` (helper de test): captura notificaciones de procesamiento y excepciones, con métodos determinísticos `awaitProcessed`, `awaitProcessedCount` y `awaitError(Duration)`.
   - `ActorProbeActor`: actor que reenvía notificaciones al `TestProbe` — útil en pruebas de extremo a extremo (E2E).
   - `ExceptionNotifierRegistry`: permite registrar listeners para excepciones lanzadas desde `receive()`.
   - Se incluyen tests unitarios y de integración (`SupervisionAndLifecycleTest`, `SupervisionE2ERestartTest`, `ChildSpawnTest`).

5. Lifecycle
   - Estados: `CREATED -> RUNNING -> STOPPING -> STOPPED`.
   - `stop()` drena la mailbox y luego marca `STOPPED`.
   - Mensajes enviados durante `STOPPING`/`STOPPED` son rechazados y contabilizados.

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

Testing y QA
------------
Sylph incluye utilidades y pruebas para validar comportamiento del runtime (lifecycle, supervisión, mailboxes). A continuación encontrarás instrucciones para ejecutar tests y usar las utilidades de prueba que añadimos.

Ejecutar tests (Gradle)
```powershell
# Ejecutar todos los tests (unitarios + integration/e2e)
.\gradlew.bat test --no-daemon

# Ejecutar tests específicos
.\gradlew.bat test --tests "sylph.examples.SupervisionE2ERestartTest" --no-daemon
```

Utilidades de testing incluidas
- `TestProbe` (helper de test): clase de test helper que captura mensajes que los actores notifican como "procesados" y permite métodos determinísticos: `awaitProcessed`, `awaitProcessedCount` y `awaitError(Duration)`. Está en `src/test/java/sylph/testutils/TestProbe.java`. Usa try-with-resources para desregistrarse automáticamente.

- `ActorProbeActor` (actor de prueba): actor que puedes crear dentro del `ActorSystem` y que reenvía los mensajes recibidos al `TestProbe` helper. Útil para pruebas de extremo a extremo (E2E) que validan la ruta completa de mensajería (ActorRef → mailbox → actor → probe).

- `ExceptionNotifierRegistry` (runtime/main): registry en `src/main/java/sylph/testhooks/ExceptionNotifierRegistry.java` que permite registrar listeners de excepciones lanzadas dentro de `receive(...)`. Los tests se registran en este registry para implementar `awaitError`.

Notas sobre `TestHooks`
- Para compatibilidad durante el desarrollo existe un shim de tests `src/test/java/sylph/testhooks/TestHooks.java` que delega al `ExceptionNotifierRegistry`. En producción hay una clase `src/main/java/sylph/testhooks/TestHooks.java` marcada como deprecated que lanza si se la invoca, para evitar dependencias de testing en el artefacto final.

Ejemplos rápidos de uso del `TestProbe`

- Esperar que un actor haya procesado un mensaje:

```java
try (TestProbe<Msg> probe = new TestProbe<>()) {
    // ... spawn actor que llama probe.receiveFromActor(message) desde su receive() ...
    assertTrue(probe.awaitProcessedCount(2, Duration.ofSeconds(2)));
}
```

- Esperar una excepción lanzada por un actor (awaitError):

```java
try (TestProbe<Msg> probe = new TestProbe<>()) {
    target.tell(new Msg("boom")); // este mensaje hará que el actor lance
    Throwable t = probe.awaitError(Duration.ofSeconds(1));
    assertNotNull(t);
    assertEquals("boom", t.getMessage());
}
```

Estrategia de pruebas recomendada
- Usa `TestProbe` para pruebas unitarias determinísticas (life-cycle, stop-drain, supervisión local).
- Usa `ActorProbeActor` para pruebas E2E que validen la ruta completa de mensajería.
- Evita sleeps arbitrarios en tests; usa los métodos await del `TestProbe` con timeouts cortos y controlados.

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
- Implementar supervisión activa (RESTART / STOP) en el runtime (ya hay soporte básico; ampliar políticas y configuraciones).
- Bounded mailboxes y backpressure.
- `shutdownGracefully(Duration timeout)` en `ActorSystem`.
- Migrar métricas a formato Prometheus y/o integrar Micrometer.
- Más tests (stress/benchmark JMH, latencia/throughput) y CI que ejecute tests en serie para evitar contaminación por listeners globales.

Contacto / contribuciones
-------------------------
Abre issues o PR en el repositorio con propuestas. Antes de cambios grandes, propone una RFC en un issue para discusión.

Licencia
--------
- Por definir


---
*README actualizado con secciones de testing y notas sobre TestProbe/ActorProbe/ExceptionNotifierRegistry*
