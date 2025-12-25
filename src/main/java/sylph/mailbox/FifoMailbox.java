package sylph.mailbox;

import sylph.interfaces.mailbox.Mailbox;
import sylph.interfaces.message.Message;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.List;

public class FifoMailbox implements Mailbox {
    private final BlockingQueue<Message> queue;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public FifoMailbox() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public FifoMailbox(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void put(Message message) throws InterruptedException {
        if (closed.get()) {
            // If closed, silently drop.
            return;
        }
        queue.put(message);
    }

    @Override
    public Message take() throws InterruptedException {
        // If closed and empty, return null to indicate no more messages.
        if (closed.get() && queue.isEmpty()) return null;
        return queue.take();
    }

    @Override
    public Message poll() {
        return queue.poll();
    }

    @Override
    public void close() {
        closed.set(true);
    }

    @Override
    public int discardPending() {
        List<Message> drained = new ArrayList<>();
        return queue.drainTo(drained);
    }
}
