# Distributed Loom Actors (Práctica)

Este repositorio contiene un proyecto práctico donde se construye un **runtime de actores** utilizando **Java 21** y **Virtual Threads (Project Loom)**. El objetivo es aprender los fundamentos detrás de sistemas como Akka/Pekko, Orbit u Orleans, pero implementados de forma ligera, moderna y transparente.

## 🚀 Objetivo del proyecto

El propósito principal es **entender y construir desde cero**:

* Actores locales basados en Virtual Threads
* Mailboxes y procesamiento secuencial de mensajes
* Aislamiento de estado y concurrencia segura
* Sharding lógico de actores
* Comunicación remota (futuro: gRPC, Kafka u otro transporte)
* Conceptos esenciales para sistemas distribuidos

Este repo NO busca ser un framework completo, sino una **base educativa** y extensible.

---

## 🧩 Características principales

* Implementación limpia con **Java 21 estándar** (sin Spring Boot)
* Actores con **loop interno y mailbox** basado en `BlockingQueue`
* Ejecución en **Virtual Threads** para permitir miles de actores concurrentes
* Arquitectura modular para extender con:

  * gRPC para actores remotos
  * Kafka para sharding distribuido
  * Persistencia tipo Event Sourcing

---

## 📁 Estructura del proyecto

```
src/
  main/java/com/sebastian/actors/
    runtime/
      Actor.java
      ActorRef.java
      Mailbox.java
      VirtualThreadActor.java
      Supervisor.java
    cluster/
      Sharding.java
      ActorDirectory.java
    transport/
      grpc/
      kafka/
    examples/
      CounterActor.java
```

---

## 🧪 Ejemplo básico

Un actor simple que procesa mensajes:

```java
Actor<String> actor = Actors.spawn(msg -> {
    System.out.println("Procesando: " + msg);
});

actor.tell("Hola");
actor.tell("Mundo");
```

---

## 🎯 Roadmap

### ✔ Etapa 1 — Actores locales

* [x] Actor con mailbox
* [x] Ejecución en Virtual Thread
* [x] ActorRef para abstracción de envío

### ⏳ Etapa 2 — Extender a distribución

* [ ] Sharding básico basado en actorId
* [ ] RPC con gRPC
* [ ] ActorDirectory para descubrir ubicación de actores

### 🔜 Etapa 3 — Persistencia y resiliencia

* [ ] Snapshots
* [ ] Event Sourcing
* [ ] Supervision Strategies (restart, stop)

---

## 🧠 ¿Por qué Virtual Threads?

Project Loom permite modelos como actores sin necesidad de frameworks pesados. Cada actor puede tener su propio hilo ligero, manteniendo orden, aislamiento y simplicidad.

Beneficios:

* Código imperativo, fácil de leer
* Concurrencia de alto volumen sin bloquear
* Menos complejidad comparado con modelos reactivos

---

## 🤝 Contribuciones

Este es un proyecto de práctica personal, pero cualquier sugerencia, issue o mejora es bienvenida.

---

## 📜 Licencia

Por definir...

---

## ✨ Autor

Sebastián Díaz — Practicando arquitectura distribuida moderna con Java 21 y actores basados en Virtual Threads.
