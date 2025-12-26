package sylph.api.internal;

import sylph.api.*;
import java.util.Objects;
import java.util.function.Supplier;

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

    @Override
    public <C> ChildSpawnBuilder<C> spawnChild(Supplier<Actor<C>> actorSupplier) {
        Objects.requireNonNull(actorSupplier, "actorSupplier");
        return new DefaultChildSpawnBuilder<>(selfRef, actorSupplier);
    }
}
