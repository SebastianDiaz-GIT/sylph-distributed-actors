package sylph.actors;

import sylph.actors.records.ActorId;
import sylph.util.logging.Logger;
import sylph.util.metrics.ActorMetrics;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Sistema de actores que administra la creación y registro de actores.
 */
public class ActorSystemImpl {
    private final Logger logger = Logger.getLogger(ActorSystemImpl.class);
    private final Map<ActorId, ActorRefImpl> actors = new ConcurrentHashMap<>();
    private final Map<String, ActorId> names = new ConcurrentHashMap<>();

    public ActorRefImpl actorOf(String name, BasicActorImpl actor) {
        ActorId actorId = new ActorId(UUID.randomUUID());
        ActorRefImpl ref = new ActorRefImpl(actor, actorId);
        actors.put(actorId, ref);
        if (name != null) {
            names.put(name, actorId);
        }
        logger.info("Spawned actor " + actorId.value() + (name != null ? " as '" + name + "'" : ""));
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
        ActorId actorId = names.remove(name);
        if (actorId != null) {
            ActorRefImpl ref = actors.remove(actorId);
            if (ref != null) ref.stop();
        }
    }

    /**
     * Detiene todos los actores en el sistema.
     */
    public void stopAll() {
        actors.values().forEach(ActorRefImpl::stop);
        names.clear();
        actors.clear();
    }

    /**
     * Devuelve las métricas asociadas a un actor por nombre (si existe).
     */
    public ActorMetrics getMetrics(String name) {
        ActorRefImpl ref = getActor(name);
        return ref != null ? ref.metrics() : null;
    }

    /**
     * Obtiene métricas de todos los actores en el sistema. La clave es el nombre
     * si existe, o el id (UUID) en caso contrario.
     */
    public Map<String, ActorMetrics> getAllMetrics() {
        // invertimos el mapa names para obtener nombre por actorId
        Map<ActorId, String> idToName = names.entrySet().stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getValue, Map.Entry::getKey));

        return actors.entrySet().stream().collect(Collectors.toConcurrentMap(
                e -> idToName.getOrDefault(e.getKey(), e.getKey().value().toString()),
                e -> e.getValue().metrics()
        ));
    }

    /**
     * Registra (logger.info) las métricas actuales de todos los actores.
     */
    public void logAllMetrics() {
        Map<String, ActorMetrics> all = getAllMetrics();
        all.forEach((k, v) -> logger.info("metrics[" + k + "] = " + v.toString()));
    }
}
