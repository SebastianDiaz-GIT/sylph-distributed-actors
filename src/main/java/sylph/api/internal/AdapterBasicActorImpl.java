package sylph.api.internal;

import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.interfaces.mailbox.Mailbox;
import sylph.interfaces.message.Message;
import sylph.mailbox.FifoMailbox;
import sylph.actors.BasicActorImpl;

/**
 * Adapter que convierte un Actor de la API pública en un BasicActor interno.
 * El hilo del actor se inicia de forma diferida para permitir fijar la
 * referencia interna (delegate) antes de procesar mensajes.
 */
public final class AdapterBasicActorImpl<M> extends BasicActorImpl {
    private final Actor<M> actor;
    private final ApiActorRefImpl<M> publicRef;
    private final ActorContext<M> ctx;

    public AdapterBasicActorImpl(Actor<M> actor, ApiActorRefImpl<M> publicRef) {
        super(new FifoMailbox(), false);
        this.actor = actor;
        this.publicRef = publicRef;
        this.ctx = new ActorContextImpl<>(publicRef);
    }

    /**
     * Nuevo constructor que acepta una mailbox personalizada (FIFO, PRIORITY, etc.).
     */
    public AdapterBasicActorImpl(Actor<M> actor, ApiActorRefImpl<M> publicRef, Mailbox mailbox) {
        super(mailbox, false);
        this.actor = actor;
        this.publicRef = publicRef;
        this.ctx = new ActorContextImpl<>(publicRef);
    }

    @Override
    protected void onReceive(Message message) {
        // Confiamos en que solo enviamos ApiMessage hacia actores adaptados.
        @SuppressWarnings("unchecked")
        ApiMessage<M> apiMsg = (ApiMessage<M>) message;
        M payload = apiMsg.payload();
        try {
            actor.receive(payload, ctx);
        } catch (Throwable t) {
            // No propagar excepciones fuera del actor; preparar supervisión futura.
            t.printStackTrace();
        }
    }

    public void start() {
        startActorLoop();
    }
}
