package sylph.actors;

import sylph.enums.LifecycleState;
import sylph.interfaces.mailbox.Mailbox;
import sylph.mailbox.FifoMailbox;
import sylph.interfaces.message.Message;
import sylph.util.logging.Logger;
import sylph.util.metrics.ActorMetrics;

/**
 * Actor básico que procesa mensajes de tipo Message en un hilo virtual.
 * Ahora soporta mailboxes plugin-based y permite inicio diferido del bucle
 * para que adaptadores puedan inicializar contexto antes de arrancar.
 */
public class BasicActorImpl {
    private final Mailbox mailbox;
    private volatile Thread actorThread;
    private volatile LifecycleState state = LifecycleState.CREATED;
    private final Logger logger = Logger.getLogger(BasicActorImpl.class);
    private final ActorMetrics metrics = new ActorMetrics();

    public BasicActorImpl() {
        this(new FifoMailbox(), true);
    }

    public BasicActorImpl(Mailbox mailbox) {
        this(mailbox, true);
    }

    /**
     * Construye el actor con opción de iniciar inmediatamente o no.
     * Cuando startImmediately es false, el hilo no arrancará hasta
     * que se llame a {@link #startActorLoop()}.
     */
    protected BasicActorImpl(Mailbox mailbox, boolean startImmediately) {
        this.mailbox = mailbox;
        if (startImmediately) {
            startActorLoop();
        }
    }

    /**
     * Inicia el bucle del actor en un hilo virtual. Idempotente.
     */
    protected synchronized void startActorLoop() {
        if (actorThread != null) return;
        state = LifecycleState.RUNNING;
        metrics.setState(state);
        actorThread = Thread.ofVirtual().start(() -> {
            while (state == LifecycleState.RUNNING) {
                try {
                    Message message = mailbox.take();
                    try {
                        onReceive(message);
                        metrics.incrementProcessed();
                    } catch (Throwable t) {
                        logger.error("Error processing message", t);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /**
     * Sends a message to this actor's mailbox.
     * This method blocks if the mailbox is full.
     * If the actor is stopping or stopped, the message will be rejected.
     *
     * @param message the message to send
     */
    public void send(Message message) {
        if (state == LifecycleState.STOPPING || state == LifecycleState.STOPPED) {
            // Reject messages when stopping/stopped. Could log or throw in future.
            metrics.incrementRejected();
            return;
        }
        try {
            mailbox.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Optionally log or handle interruption
            logger.warn("Interrupted while sending message");
        }
    }

    protected void onReceive(Message message) {
        // Implementar en subclases
    }

    /**
     * Inicia el proceso de parada: pasa a STOPPING, drena la mailbox procesando
     * los mensajes pendientes, y luego marca STOPPED.
     */
    public synchronized void stop() {
        if (state == LifecycleState.STOPPING || state == LifecycleState.STOPPED) return;
        state = LifecycleState.STOPPING;
        metrics.setState(state);
        logger.info("Actor entering STOPPING");

        // Procesar mensajes pendientes sin aceptar nuevos
        Message pending;
        while ((pending = mailbox.poll()) != null) {
            try {
                onReceive(pending);
                metrics.incrementProcessed();
            } catch (Throwable t) {
                logger.error("Error processing pending message during stop", t);
            }
        }

        state = LifecycleState.STOPPED;
        metrics.setState(state);
        logger.info("Actor stopped");
        Thread t = actorThread;
        if (t != null) {
            t.interrupt();
        }
    }

    public ActorMetrics getMetrics() {
        return metrics;
    }
}
