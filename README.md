SYLPH — Sistema de Actores (v0.1)
=================================

Resumen
-------
Sylph es un runtime de actores local-first para Java, orientado a facilitar la creación de aplicaciones concurrentes usando el modelo de actores.
Esta versión (v0.1) provee:
- API pública mínima y tipada: `Actor`, `ActorRef<M>`, `ActorContext<M>`, `ActorSystem`.
- Implementación local por defecto (`DefaultActorSystem`).
- Mailbox FIFO y adaptadores para integrar la API pública con el runtime interno.
- Ejemplos: `PrinterDemo`, `PrinterDemoKeepAlive`, `PrinterDemoAutoShutdown`.

Principios
----------
- Un actor procesa un mensaje a la vez.
- Mensajes inmutables (usa `record` o tipos inmutables para tus mensajes).
- La API pública no expone hilos, executors ni detalles de concurrencia.
- Virtual Threads (Project Loom) se usan internamente en el runtime.

Contenido del repositorio
-------------------------
- `src/main/java/sylph/api` - API pública (interfaces y fábrica).
- `src/main/java/sylph/actors` - runtime interno (implementación y referencias internas).
- `src/main/java/sylph/api/internal` - adaptadores entre la API pública y el runtime.
- `src/main/java/sylph/mailbox` - mailboxes (FIFO actualmente).
- `src/main/java/sylph/examples` - demos ejecutables.

Rápido inicio (ejecutar demo)
-----------------------------
Compilar el proyecto (Windows cmd.exe):

```bat
cd /d C:\developer-space\personal\sylph-distributed-actors
gradlew.bat build --no-daemon
```

Ejecutar demo simple:

```bat
java -cp build\classes\java\main sylph.examples.PrinterDemo
```

Demo que espera ENTER antes de cerrar (mantiene actores vivos):

```bat
java -cp build\classes\java\main sylph.examples.PrinterDemoKeepAlive
```

Demo con shutdown automático:

```bat
java -cp build\classes\java\main sylph.examples.PrinterDemoAutoShutdown
```

Principales abstracciones (resumen para programadores)
------------------------------------------------------
- Actor<M>
  - Interfaz que implementa la lógica de procesamiento.
  - Método principal: `void receive(M message, ActorContext<M> ctx)`.

- ActorRef<M>
  - Referencia segura para enviar mensajes: `void tell(M message)`.
  - También provee `void stop()` para solicitar la detención externa del actor.

- ActorContext<M>
  - Contexto proporcionado dentro del actor. Permite obtener `self()` y `stop()` desde el actor.

- ActorSystem
  - Punto de entrada para crear actores: `spawn(Supplier<Actor<M>>)` y `spawn(String name, Supplier<Actor<M>>)`.
  - `shutdown()` / `close()` para detener el sistema (y todos los actores).
  - Usar `ActorSystems.create()` para obtener una implementación por defecto.

Ejemplo mínimo (uso recomendado)
-------------------------------
```java
    try (ActorSystem system = ActorSystems.create()) {
        ActorRef<String> printer = system.spawn("printer", PrinterActor::new);
        printer.tell("hola");
        // ...
    }
```

Semántica de parada
-------------------
- `ActorRef.stop()`: orden externa inmediata. Forzada: el actor deja de procesar mensajes y su hilo se interrumpe.
- `tell("stop")`: es un mensaje normal; el actor recibirá el mensaje en `receive` y puede decidir limpiar y llamar a `ctx.stop()` para detenerse desde dentro. Esta opción es "graciosa" desde la perspectiva del actor.

Integración en aplicaciones gestionadas (p. ej. Spring Boot)
----------------------------------------------------------
- Registra `ActorSystem` como un bean singleton y cierra el sistema en el hook de shutdown del contenedor (@PreDestroy o DisposableBean).
- Alternativamente, instala un shutdown hook con `Runtime.getRuntime().addShutdownHook(...)` para asegurar un cierre ordenado ante SIGTERM.

Buenas prácticas
----------------
- Mantén los mensajes inmutables (usar `record`).
- Evita exponer hilos y ejecutores en la API pública.
- Prefiere `tell` con mensajes semánticos en vez de strings crudos para controlar comportamientos.
- Documenta claramente si `stop()` es forzado o gracioso.

Siguientes pasos sugeridos (v0.2)
--------------------------------
- Supervisión y políticas de reinicio.
- Bounded mailboxes y backpressure.
- `shutdownGracefully(Duration timeout)` en `ActorSystem`.
- Instrumentación y métricas por actor.

Contacto / contribuciones
-------------------------
Abre issues o PR en el repositorio con propuestas. Antes de cambios grandes, propon una RFC en un issue para discusión.

Licencia
--------
- (Añade aquí la licencia que prefieras, por ejemplo MIT)

