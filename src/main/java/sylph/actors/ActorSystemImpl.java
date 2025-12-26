package sylph.actors;

import sylph.actors.records.ActorId;
import sylph.util.logging.Logger;
import sylph.util.metrics.ActorMetrics;

import java.util.Map;
import java.util.Set;
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
    // Relación padre -> conjunto de hijos
    private final Map<ActorId, Set<ActorId>> children = new ConcurrentHashMap<>();

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
     * Crea un actor hijo asociado al padre proporcionado. Registra la relación
     * padre->hijo para permitir paradas en cascada y supervisión.
     */
    public ActorRefImpl actorOfChild(ActorRefImpl parent, String name, BasicActorImpl child) {
        ActorId actorId = new ActorId(UUID.randomUUID());
        ActorRefImpl ref = new ActorRefImpl(child, actorId);
        actors.put(actorId, ref);
        // Registrar por nombre si se proporcionó
        if (name != null) {
            names.put(name, actorId);
        }
        // Registrar relación padre->hijo
        if (parent != null) {
            ActorId pid = parent.id();
            children.computeIfAbsent(pid, k -> ConcurrentHashMap.newKeySet()).add(actorId);
        }
        logger.info("Spawned child actor " + actorId.value() + (name != null ? " as '" + name + "'" : "") + (parent != null ? " parent=" + parent.id().value() : ""));
        // Si se creó como adapter con start deferred, intentar iniciar el actor loop
        try {
            child.startActorLoop();
        } catch (Throwable ignore) {
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
     * Detiene también recursivamente sus hijos si los tuviera.
     *
     * @param name Nombre del actor a detener.
     */

    public void stopActor(String name) {
        ActorId actorId = names.remove(name);
        if (actorId != null) {
            ActorRefImpl ref = actors.remove(actorId);
            // parar hijos recursivamente
            stopChildrenRecursive(actorId);
            if (ref != null) ref.stop();
        }
    }

    private void stopChildrenRecursive(ActorId parentId) {
        Set<ActorId> childs = children.remove(parentId);
        if (childs == null) return;
        for (ActorId cid : childs) {
            ActorRefImpl cref = actors.remove(cid);
            // borrar nombres asociados
            names.entrySet().removeIf(e -> e.getValue().equals(cid));
            // parar recursivamente
            stopChildrenRecursive(cid);
            if (cref != null) cref.stop();
        }
    }

    /**
     * Detiene todos los actores en el sistema.
     */
    public void stopAll() {
        // Detener todos los actores (parada recursiva segura)
        actors.values().forEach(ActorRefImpl::stop);
        names.clear();
        actors.clear();
        children.clear();
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
