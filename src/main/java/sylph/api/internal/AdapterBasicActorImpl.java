package sylph.api.internal;

import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.enums.Supervision;
import sylph.interfaces.mailbox.Mailbox;
import sylph.interfaces.message.Message;
import sylph.mailbox.FifoMailbox;
import sylph.actors.BasicActorImpl;
import sylph.util.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Adapter que convierte un Actor de la API pública en un BasicActor interno.
 * El hilo del actor se inicia de forma diferida para permitir fijar la
 * referencia interna (delegate) antes de procesar mensajes.
 */
public final class AdapterBasicActorImpl<M> extends BasicActorImpl {
    private final Supplier<Actor<M>> actorSupplier;
    private volatile Actor<M> actor;
    private final ApiActorRefImpl<M> publicRef;
    private final ActorContext<M> ctx;
    private final Supervision supervision;
    private final Logger logger = Logger.getLogger(AdapterBasicActorImpl.class);
    // Cuando se solicita parada por supervisión STOP, marcamos este flag para
    // que mensajes ya devueltos por mailbox.take() pero no procesados se descarten.
    private volatile boolean stoppingRequested = false;

    public AdapterBasicActorImpl(Supplier<Actor<M>> actorSupplier, ApiActorRefImpl<M> publicRef, Supervision supervision) {
        super(new FifoMailbox(), false);
        this.actorSupplier = Objects.requireNonNull(actorSupplier);
        this.actor = Objects.requireNonNull(actorSupplier.get());
        this.publicRef = publicRef;
        this.ctx = new ActorContextImpl<>(publicRef);
        this.supervision = supervision == null ? Supervision.NONE : supervision;
    }

    /**
     * Nuevo constructor que acepta una mailbox personalizada (FIFO, PRIORITY, etc.).
     */
    public AdapterBasicActorImpl(Supplier<Actor<M>> actorSupplier, ApiActorRefImpl<M> publicRef, Mailbox mailbox, Supervision supervision) {
        super(mailbox, false);
        this.actorSupplier = Objects.requireNonNull(actorSupplier);
        this.actor = Objects.requireNonNull(actorSupplier.get());
        this.publicRef = publicRef;
        this.ctx = new ActorContextImpl<>(publicRef);
        this.supervision = supervision == null ? Supervision.NONE : supervision;
    }

    @Override
    protected void onReceive(Message message) {
        // Confiamos en que solo enviamos ApiMessage hacia actores adaptados.
        @SuppressWarnings("unchecked")
        ApiMessage<M> apiMsg = (ApiMessage<M>) message;
        M payload = apiMsg.payload();
        // Si ya se solicitó parada por supervisión STOP, rechazamos el mensaje
        // incluso si ya fue extraído de la mailbox (evita procesar in-flight).
        if (stoppingRequested) {
            logger.info("Dropping in-flight message due supervision STOP: " + payload);
            // incrementar métrica de rechazados
            try { getMetrics().incrementRejected(); } catch (Throwable ignore) {}
            return;
        }
        try {
            actor.receive(payload, ctx);
        } catch (Throwable t) {
            // Manejo básico de supervisión según la política configurada.
            logger.error("Error in actor receive", t);
            if (supervision == Supervision.RESTART) {
                // Crear una nueva instancia del actor y continuar procesando.
                try {
                    this.actor = Objects.requireNonNull(actorSupplier.get());
                    logger.info("Actor instance restarted by supervision policy");
                    // NOTE: no reiniciamos el hilo porque el loop es el mismo; solo reemplazamos la instancia.
                } catch (Throwable t2) {
                    logger.error("Failed to recreate actor during restart supervision", t2);
                    // Si la re-creación falla, detener el actor para evitar bucle de errores.
                    this.stop();
                }
            } else if (supervision == Supervision.STOP) {
                // Parar el actor y descartar la mailbox (no procesar pendientes).
                logger.info("Stopping actor due to supervision policy (drop pending)");
                // indicar que queremos descartar también mensajes que fueron ya 'take()' pero no procesados
                stoppingRequested = true;
                this.stopDropPending();
            }
        }
    }

    public void start() {
        logger.info("Starting adapter actor loop (publicRef=" + publicRef + ")");
        startActorLoop();
    }
}
