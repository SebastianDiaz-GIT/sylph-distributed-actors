package sylph.actors;

import sylph.interfaces.mailbox.Mailbox;
import sylph.mailbox.FifoMailbox;
import sylph.interfaces.message.Message;

/**
 * Actor básico que procesa mensajes de tipo Message en un hilo virtual.
 * Ahora soporta mailboxes plugin-based.
 */
public class BasicActor {
    private final Mailbox mailbox;
    private final Thread actorThread;
    private volatile boolean running = true;

    public BasicActor() {
        this(new FifoMailbox());
    }

    public BasicActor(Mailbox mailbox) {
        this.mailbox = mailbox;
        actorThread = Thread.ofVirtual().start(() -> {
            while (running) {
                try {
                    Message message = mailbox.take();
                    onReceive(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
        actorThread.interrupt();
    }
}
