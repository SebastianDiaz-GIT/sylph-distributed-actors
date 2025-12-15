package sylph.actors;

import sylph.interfaces.message.Message;

/**
 * Referencia segura para interactuar con un actor.
 */
public class ActorRefImpl {
    private final BasicActorImpl actor;

    public ActorRefImpl(BasicActorImpl actor) {
        this.actor = actor;
    }

    public void send(Message message) {
        actor.send(message);
    }

    public void stop() {
        actor.stop();
    }
}

