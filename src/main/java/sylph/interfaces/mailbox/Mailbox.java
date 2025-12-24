package sylph.interfaces.mailbox;

import sylph.interfaces.message.Message;

public interface Mailbox {
    void put(Message message) throws InterruptedException;
    Message take() throws InterruptedException;
    /**
     * Non-blocking poll of the mailbox. Returns null if empty.
     */
    Message poll();
}
