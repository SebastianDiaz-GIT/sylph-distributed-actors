package sylph.testutils;

import sylph.api.ActorRef;
import sylph.testhooks.ExceptionNotifierRegistry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TestProbe simple para capturar mensajes y permitir esperas deterministas.
 */
@SuppressWarnings("unused")
public class TestProbe<M> implements ActorRef<M>, ExceptionNotifierRegistry.ExceptionListener<M>, AutoCloseable {
    private final BlockingQueue<M> queue = new ArrayBlockingQueue<>(1024);
    private final List<M> processed = new ArrayList<>();
    private final BlockingQueue<Throwable> errors = new ArrayBlockingQueue<>(64);

    public TestProbe() {
        ExceptionNotifierRegistry.registerExceptionListener(this);
    }

    @Override
    public void close() {
        ExceptionNotifierRegistry.unregisterExceptionListener(this);
    }

    @Override
    public void tell(M message) {
        boolean offered = queue.offer(message);
        if (!offered) {
            // queue full: best-effort drop
            int ignored = 0;
        }
    }

    @Override
    public void stop() {
        // no-op
    }

    /**
     * Función usada por los actores de prueba para notificar que procesaron un mensaje.
     */
    public void receiveFromActor(M message) {
        synchronized (processed) {
            processed.add(message);
        }
        // also offer to queue for external waits
        boolean offered = queue.offer(message);
        if (!offered) {
            // queue full: drop
            int ignored = 0;
        }
    }

    public M expectMessage(Duration timeout) throws InterruptedException {
        return queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public boolean awaitProcessed(M expected, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            synchronized (processed) {
                if (processed.contains(expected)) return true;
            }
            // try to poll queue briefly to give chance for actor to publish
            queue.poll(10, TimeUnit.MILLISECONDS);
        }
        return false;
    }

    public boolean awaitProcessedCount(int expectedCount, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            synchronized (processed) {
                if (processed.size() >= expectedCount) return true;
            }
            queue.poll(10, TimeUnit.MILLISECONDS);
        }
        return false;
    }

    public void assertProcessed(M expected) {
        synchronized (processed) {
            if (!processed.contains(expected)) {
                throw new AssertionError("Expected processed to contain: " + expected + " but was: " + processed);
            }
        }
    }

    public void assertNotProcessed(M expected) {
        synchronized (processed) {
            if (processed.contains(expected)) {
                throw new AssertionError("Expected processed NOT to contain: " + expected + " but was: " + processed);
            }
        }
    }

    public List<M> getProcessed() {
        synchronized (processed) {
            return List.copyOf(processed);
        }
    }

    // ExceptionListener implementation
    @Override
    public void onActorException(Object publicRef, M message, Throwable t) {
        // record exception for awaitError
        boolean offered = errors.offer(t);
        if (!offered) {
            int ignored = 0;
        }
    }

    /**
     * Await an exception (error) that happened in any actor. Returns the throwable or null on timeout.
     */
    public Throwable awaitError(Duration timeout) throws InterruptedException {
        return errors.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
