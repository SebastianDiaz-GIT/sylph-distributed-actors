package sylph.api.internal;

import sylph.api.ActorContext;
import sylph.api.ActorRef;

/**
 * Implementación interna del ActorContext público.
 */
public final class ActorContextImpl<M> implements ActorContext<M> {
    private final ApiActorRefImpl<M> selfRef;

    public ActorContextImpl(ApiActorRefImpl<M> selfRef) {
        this.selfRef = selfRef;
    }

    @Override
    public ActorRef<M> self() {
        return selfRef;
    }

    @Override
    public void stop() {
        selfRef.stop();
    }
}

