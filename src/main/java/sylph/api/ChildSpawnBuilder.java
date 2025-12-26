package sylph.api;

import sylph.enums.MailboxType;
import sylph.enums.Supervision;

/**
 * Builder público para crear actores hijos desde un ActorContext.
 */
public interface ChildSpawnBuilder<M> {
    ChildSpawnBuilder<M> withName(String name);
    ChildSpawnBuilder<M> withMailbox(MailboxType mailboxType);
    ChildSpawnBuilder<M> withSupervision(Supervision supervision);
    ActorRef<M> start();
}

