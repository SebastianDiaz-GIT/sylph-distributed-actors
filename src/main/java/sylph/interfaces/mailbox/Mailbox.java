package sylph.interfaces.mailbox;

import sylph.interfaces.message.Message;

public interface Mailbox {
    void put(Message message) throws InterruptedException;
    Message take() throws InterruptedException;
    /**
     * Non-blocking poll of the mailbox. Returns null if empty.
     */
    Message poll();

    /**
     * Marks the mailbox as closed to not deliver more messages.
     * Implementations must make the closure visible to `take()`/`put()`.
     */
    default void close() {}

    /**
     * Discards all pending messages and returns the discarded amount.
     * Implementations must increment metrics if applicable.
     */
    default int discardPending() { return 0; }
}
