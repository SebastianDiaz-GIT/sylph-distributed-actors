package sylph.api;

import sylph.api.internal.AdapterBasicActorImpl;
import sylph.api.internal.ApiActorRefImpl;
import sylph.enums.MailboxType;
import sylph.enums.Supervision;
import sylph.interfaces.mailbox.Mailbox;
import sylph.mailbox.FifoMailbox;
import sylph.mailbox.PriorityMailbox;
import sylph.actors.BasicActorImpl;
import sylph.actors.ActorRefImpl;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Implementación interna del builder para spawnChild.
 */
public final class DefaultChildSpawnBuilder<M> implements ChildSpawnBuilder<M> {
    private final ApiActorRefImpl<?> parentRef;
    private final Supplier<Actor<M>> actorSupplier;
    private String name;
    private MailboxType mailboxType = MailboxType.FIFO;
    private Supervision supervision = Supervision.NONE;
    private Boolean dieAfterProcess = false;

    public DefaultChildSpawnBuilder(ApiActorRefImpl<?> parentRef, Supplier<Actor<M>> actorSupplier) {
        this.parentRef = Objects.requireNonNull(parentRef);
        this.actorSupplier = Objects.requireNonNull(actorSupplier);
    }

    @Override
    public ChildSpawnBuilder<M> withName(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public ChildSpawnBuilder<M> withMailbox(MailboxType mailboxType) {
        this.mailboxType = Objects.requireNonNull(mailboxType);
        return this;
    }

    @Override
    public ChildSpawnBuilder<M> withSupervision(Supervision supervision) {
        this.supervision = Objects.requireNonNull(supervision);
        return this;
    }

    @Override
    public ActorRef<M> start() {
        // Construir la mailbox según el tipo
        Mailbox mailbox = createMailbox(mailboxType);

        // Crear la ApiActorRef pública del hijo
        ApiActorRefImpl<M> childPublicRef = new ApiActorRefImpl<>();

        // Crear adapter con las opciones seleccionadas
        Supplier<BasicActorImpl> childWrapperSupplier = () -> new AdapterBasicActorImpl<>(actorSupplier, childPublicRef, mailbox, supervision);

        String resolvedName = (name != null) ? name : "child-" + java.util.UUID.randomUUID();

        ActorRefImpl internal = parentRef.spawnChildDelegate(childWrapperSupplier, resolvedName);

        // Asociar delegate y systemDelegate
        childPublicRef.setDelegate(internal);
        childPublicRef.setSystemDelegate(parentRef.getSystemDelegate());

        return childPublicRef;
    }

    private Mailbox createMailbox(MailboxType mailboxType) {
        if (mailboxType == MailboxType.PRIORITY) return new PriorityMailbox();
        return new FifoMailbox();
    }
}

