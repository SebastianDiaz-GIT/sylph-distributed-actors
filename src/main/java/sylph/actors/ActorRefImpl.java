package sylph.actors;

import sylph.actors.records.ActorId;
import sylph.interfaces.message.Message;
import sylph.util.logging.Logger;
import sylph.util.metrics.ActorMetrics;

/**
 * Referencia segura para interactuar con un actor.
 */
public class ActorRefImpl {
    private final BasicActorImpl actor;
    private final ActorId id;
    private final Logger logger = Logger.getLogger(ActorRefImpl.class);

    public ActorRefImpl(BasicActorImpl actor, ActorId id) {
        this.actor = actor;
        this.id = id;
    }

    public void send(Message message) {
        if (actor == null) return;
        actor.send(message);
    }

    public void stop() {
        logger.info("Stopping actor " + id.value());
        actor.stop();
    }

    public ActorMetrics metrics() {
        return actor != null ? actor.getMetrics() : null;
    }

    /**
     * Exponer id para permitir registrar relaciones padre->hijo.
     */
    public ActorId id() {
        return id;
    }
}
