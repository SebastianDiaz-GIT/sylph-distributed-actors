package sylph.api;

import sylph.actors.ActorRefImpl;
import sylph.actors.ActorSystemImpl;
import sylph.api.internal.AdapterBasicActorImpl;
import sylph.api.internal.ApiActorRefImpl;
import sylph.interfaces.mailbox.Mailbox;
import sylph.mailbox.FifoMailbox;
import sylph.mailbox.PriorityMailbox;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Implementación por defecto (local) del API {@link ActorSystem} de SYLPH.
 * NOTA: Esta clase es una implementación concreta y no forma parte de la API
 * estable expuesta al usuario; para crear un {@link ActorSystem} debe usarse
 * {@code ActorSystems.create()}.
 */
final class DefaultActorSystem implements ActorSystem, AutoCloseable {
    private final ActorSystemImpl delegate;

    DefaultActorSystem() {
        this.delegate = new ActorSystemImpl();
    }

    /**
     * Factory method para crear un ActorSystem.
     */
    public static DefaultActorSystem create() {
        return new DefaultActorSystem();
    }

    @Override
    public <M> ActorRef<M> spawn(Supplier<Actor<M>> actorSupplier) {
        // Delegar a la versión con nombre generando uno único
        String name = "actor-" + UUID.randomUUID();
        return spawn(name, actorSupplier, MailboxType.FIFO);
    }

    @Override
    public <M> ActorRef<M> spawn(String name, Supplier<Actor<M>> actorSupplier) {
        // Por compatibilidad, delegamos a la versión que acepta MailboxType
        return spawn(name, actorSupplier, MailboxType.FIFO);
    }

    @Override
    public <M> ActorRef<M> spawn(String name, Supplier<Actor<M>> actorSupplier, MailboxType mailboxType) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(actorSupplier, "actorSupplier");
        Objects.requireNonNull(mailboxType, "mailboxType");

        Actor<M> apiActor = actorSupplier.get();
        ApiActorRefImpl<M> publicRef = new ApiActorRefImpl<>();

        // Seleccionar la mailbox según el tipo público
        Mailbox mailbox;
        switch (mailboxType) {
            case FIFO:
                mailbox = new FifoMailbox();
                break;
            case PRIORITY:
                mailbox = new PriorityMailbox();
                break;
            default:
                mailbox = new FifoMailbox();
        }

        AdapterBasicActorImpl<M> adapter = new AdapterBasicActorImpl<>(apiActor, publicRef, mailbox);
        // Registrar en el sistema interno con el nombre proporcionado
        ActorRefImpl internalRef = delegate.actorOf(name, adapter);
        // Asignar el delegate para que la ApiActorRef pueda enviar mensajes
        publicRef.setDelegate(internalRef);
        // Iniciar el bucle del actor ahora que la referencia interna está lista
        adapter.start();
        return publicRef;
    }

    @Override
    public void shutdown() {
        delegate.stopAll();
    }

    @Override
    public void close() {
        shutdown();
    }
}
