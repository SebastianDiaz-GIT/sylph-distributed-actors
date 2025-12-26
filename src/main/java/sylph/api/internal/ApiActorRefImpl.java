package sylph.api.internal;

import sylph.actors.ActorRefImpl;
import sylph.api.ActorRef;
import sylph.actors.ActorSystemImpl;
import sylph.actors.BasicActorImpl;
import java.util.function.Supplier;

/**
 * Implementación interna de la referencia pública a un actor.
 */
public final class ApiActorRefImpl<M> implements ActorRef<M> {
    private volatile ActorRefImpl delegate;
    // referencia al sistema interno para operaciones como spawnChild
    private volatile ActorSystemImpl systemDelegate;

    public void setDelegate(ActorRefImpl delegate) {
        this.delegate = delegate;
    }

    /**
     * Internal setter to provide access to ActorSystemImpl for spawnChild
     */
    public void setSystemDelegate(ActorSystemImpl systemDelegate) {
        this.systemDelegate = systemDelegate;
    }

    public ActorSystemImpl getSystemDelegate() {
        return this.systemDelegate;
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

    /**
     * Usado por ActorContextImpl.spawnChild para delegar la creación del hijo
     * al ActorSystemImpl. El nombre generado es único (con prefijo 'child-').
     */
    public ActorRefImpl spawnChildDelegate(Supplier<BasicActorImpl> childActorSupplier, String name) {
        ActorSystemImpl sys = this.systemDelegate;
        if (sys == null) throw new IllegalStateException("ActorSystem not available for spawnChild");
        return sys.actorOfChild(this.delegate, name, childActorSupplier.get());
    }
}
