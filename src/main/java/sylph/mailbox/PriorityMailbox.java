package sylph.mailbox;

import sylph.interfaces.mailbox.Mailbox;
import sylph.interfaces.message.Message;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityMailbox implements Mailbox {
    private final PriorityBlockingQueue<Message> queue;

    public PriorityMailbox() {
        this.queue = new PriorityBlockingQueue<>();
    }

    public PriorityMailbox(int capacity) {
        this.queue = new PriorityBlockingQueue<>(capacity);
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
