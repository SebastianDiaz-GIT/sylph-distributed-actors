package sylph.api.internal;

import sylph.actors.ActorRefImpl;
import sylph.api.ActorRef;

/**
 * Implementación interna de la referencia pública a un actor.
 */
public final class ApiActorRefImpl<M> implements ActorRef<M> {
    private volatile ActorRefImpl delegate;

    public void setDelegate(ActorRefImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tell(M message) {
        System.out.println("[ApiActorRef] sending: " + message);
        ActorRefImpl d = this.delegate;
        if (d == null) {
            throw new IllegalStateException("ActorRef not initialized yet");
        }
        d.send(new ApiMessage<>(message));
    }

    @Override
    public void stop() {
        ActorRefImpl d = this.delegate;
        if (d != null) d.stop();
    }
}
