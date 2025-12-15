package sylph.actors;

import sylph.interfaces.mailbox.Mailbox;
import sylph.mailbox.FifoMailbox;
import sylph.interfaces.message.Message;

/**
 * Actor básico que procesa mensajes de tipo Message en un hilo virtual.
 * Ahora soporta mailboxes plugin-based y permite inicio diferido del bucle
 * para que adaptadores puedan inicializar contexto antes de arrancar.
 */
public class BasicActorImpl {
    private final Mailbox mailbox;
    private volatile Thread actorThread;
    private volatile boolean running = true;

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
    protected void startActorLoop() {
        if (actorThread != null) return;
        actorThread = Thread.ofVirtual().start(() -> {
            while (running) {
                try {
                    Message message = mailbox.take();
                    onReceive(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable t) {
                    // No propagar excepciones fuera del actor. Preparado para supervisión futura.
                    t.printStackTrace();
                }
            }
        });
    }

    /**
     * Sends a message to this actor's mailbox.
     * This method blocks if the mailbox is full.
     *
     * @param message the message to send
     */
    public void send(Message message) {
        try {
            mailbox.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Optionally log or handle interruption
        }
    }

    protected void onReceive(Message message) {
        // Implementar en subclases
    }

    public void stop() {
        running = false;
        Thread t = actorThread;
        if (t != null) {
            t.interrupt();
        }
    }
}
