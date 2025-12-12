package sylph.mailbox;

import sylph.interfaces.mailbox.Mailbox;
import sylph.interfaces.message.Message;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FifoMailbox implements Mailbox {
    private final BlockingQueue<Message> queue;

    public FifoMailbox() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public FifoMailbox(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void put(Message message) throws InterruptedException {
        queue.put(message);
    }

    @Override
    public Message take() throws InterruptedException {
        return queue.take();
    }
}
