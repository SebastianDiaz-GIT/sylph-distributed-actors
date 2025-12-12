package sylph.actors;

import sylph.interfaces.message.Message;

/**
 * Referencia segura para interactuar con un actor.
 */
public class ActorRef {
    private final BasicActor actor;

    public ActorRef(BasicActor actor) {
        this.actor = actor;
    }

    public void send(Message message) {
        actor.send(message);
    }

    public void stop() {
        actor.stop();
    }
}

