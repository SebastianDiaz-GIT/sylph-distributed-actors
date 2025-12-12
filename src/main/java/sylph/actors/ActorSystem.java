package sylph.actors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sistema de actores que administra la creación y registro de actores.
 */
public class ActorSystem {
    private final Map<String, ActorRef> actors = new ConcurrentHashMap<>();

    public ActorRef actorOf(String name, BasicActor actor) {
        ActorRef ref = new ActorRef(actor);
        actors.put(name, ref);
        return ref;
    }

    /**
     * Obtiene la referencia de un actor por su nombre.
     *
     * @param name Nombre del actor.
     * @return Referencia del actor o null si no existe.
     */
    public ActorRef getActor(String name) {
        return actors.get(name);
    }

    /**
     * Detiene y elimina un actor del sistema.
     *
     * @param name Nombre del actor a detener.
     */

    public void stopActor(String name) {
        ActorRef ref = actors.remove(name);
        if (ref != null) {
            ref.stop();
        }
    }

    /**
     * Detiene todos los actores en el sistema.
     */
    public void stopAll() {
        actors.values().forEach(ActorRef::stop);
        actors.clear();
    }
}

