package sylph.api;

import sylph.enums.MailboxType;
import sylph.enums.Supervision;

import java.util.Objects;
import java.util.function.Supplier;

final class DefaultSpawnBuilder<M> implements SpawnBuilder<M> {
    private final DefaultActorSystem system;
    private final Supplier<Actor<M>> supplier;
    private String name;
    private MailboxType mailboxType = MailboxType.FIFO;
    private Supervision supervision = Supervision.NONE;

    DefaultSpawnBuilder(DefaultActorSystem system, Supplier<Actor<M>> supplier) {
        this.system = system;
        this.supplier = supplier;
    }

    @Override
    public SpawnBuilder<M> withName(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public SpawnBuilder<M> withMailbox(MailboxType mailboxType) {
        this.mailboxType = Objects.requireNonNull(mailboxType);
        return this;
    }

    @Override
    public SpawnBuilder<M> withSupervision(Supervision supervision) {
        this.supervision = Objects.requireNonNull(supervision);
        return this;
    }

    @Override
    public ActorRef<M> start() {
        String resolvedName = name != null ? name : "actor-" + java.util.UUID.randomUUID();
        // Por ahora la política de supervision no está implementada; se guarda para futuras extensiones
        return system.spawn(resolvedName, supplier, mailboxType);
    }
}

