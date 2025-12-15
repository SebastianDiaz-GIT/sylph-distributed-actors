package sylph.actors;

import sylph.actors.records.ActorId;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sistema de actores que administra la creación y registro de actores.
 */
public class ActorSystemImpl {
    private final Map<ActorId, ActorRefImpl> actors = new ConcurrentHashMap<>();
    private final Map<String, ActorId> names = new ConcurrentHashMap<>();

    public ActorRefImpl actorOf(String name, BasicActorImpl actor) {
        ActorId actorId = new ActorId(UUID.randomUUID());
        ActorRefImpl ref = new ActorRefImpl(actor, actorId);
        actors.put(actorId, ref);
        if (name != null) {
            names.put(name, actorId);
        }
        return ref;
    }

    /**
     * Obtiene la referencia de un actor por su nombre.
     *
     * @param name Nombre del actor.
     * @return Referencia del actor o null si no existe.
     */
    public ActorRefImpl getActor(String name) {
        ActorId actorId = names.get(name);
        return actorId != null ? actors.get(actorId) : null;
    }

    /**
     * Detiene y elimina un actor del sistema.
     *
     * @param name Nombre del actor a detener.
     */

    public void stopActor(String name) {
        ActorRefImpl ref = actors.remove(name);
        if (ref != null) {
            ref.stop();
        }
    }

    /**
     * Detiene todos los actores en el sistema.
     */
    public void stopAll() {
        actors.values().forEach(ActorRefImpl::stop);
        actors.clear();
    }
}

