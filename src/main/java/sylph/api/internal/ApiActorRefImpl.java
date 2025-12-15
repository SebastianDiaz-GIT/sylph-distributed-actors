package sylph.api.internal;

import sylph.api.ActorRef;

/**
 * Implementación interna de la referencia pública a un actor.
 */
public final class ApiActorRefImpl<M> implements ActorRef<M> {
    private volatile sylph.actors.ActorRef delegate;

    public void setDelegate(sylph.actors.ActorRef delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tell(M message) {
        System.out.println("[ApiActorRef] sending: " + message);
        sylph.actors.ActorRef d = this.delegate;
        if (d == null) {
            throw new IllegalStateException("ActorRef not initialized yet");
        }
        d.send(new ApiMessage<>(message));
    }

    @Override
    public void stop() {
        sylph.actors.ActorRef d = this.delegate;
        if (d != null) d.stop();
    }
}
