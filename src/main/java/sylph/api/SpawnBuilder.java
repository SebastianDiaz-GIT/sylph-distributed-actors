package sylph.api;

import sylph.enums.MailboxType;
import sylph.enums.Supervision;

/**
 * Fluent builder para crear actores con opciones encadenadas.
 */
public interface SpawnBuilder<M> {
    SpawnBuilder<M> withName(String name);
    SpawnBuilder<M> withMailbox(MailboxType mailboxType);
    SpawnBuilder<M> withSupervision(Supervision supervision);
    ActorRef<M> start();
}
