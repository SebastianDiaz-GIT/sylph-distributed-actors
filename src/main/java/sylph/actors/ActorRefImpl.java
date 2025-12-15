package sylph.actors;

import sylph.actors.records.ActorId;
import sylph.interfaces.message.Message;

/**
 * Referencia segura para interactuar con un actor.
 */
public class ActorRefImpl {
    private final BasicActorImpl actor;
    private final ActorId id;

    public ActorRefImpl(BasicActorImpl actor, ActorId id) {
        this.actor = actor;
        this.id = id;
    }

    public void send(Message message) {
        actor.send(message);
    }

    public void stop() {
        actor.stop();
    }
}

