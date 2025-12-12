package sylph.interfaces.mailbox;

import sylph.interfaces.message.Message;

public interface Mailbox {
    void put(Message message) throws InterruptedException;
    Message take() throws InterruptedException;
}

