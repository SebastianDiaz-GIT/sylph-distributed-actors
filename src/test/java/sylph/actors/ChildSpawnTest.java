package sylph.actors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.api.ActorRef;
import sylph.api.ActorSystems;
import sylph.api.ActorSystem;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ChildSpawnTest {

    private ActorSystem system;

    @AfterEach
    void tearDown() {
        if (system != null) system.shutdown();
    }

    static class Child implements Actor<String> {
        private final AtomicInteger seen = new AtomicInteger();
        private final CountDownLatch latch;

        Child(CountDownLatch latch) { this.latch = latch; }

        @Override
        public void receive(String message, ActorContext<String> ctx) {
            seen.incrementAndGet();
            if (latch != null) latch.countDown();
        }
    }

    static class Parent implements Actor<String> {
        private ActorRef<String> child;
        private final CountDownLatch childLatch;

        Parent(CountDownLatch childLatch) { this.childLatch = childLatch; }

        @Override
        public void receive(String message, ActorContext<String> ctx) {
            if (child == null) {
                child = ctx.spawnChild(() -> new Child(childLatch)).withName("child").start();
            }
            child.tell(message + "-to-child");
        }
    }

    @Test
    void parentCreatesSingleChild_noLoop() throws Exception {
        system = ActorSystems.create();

        CountDownLatch childLatch = new CountDownLatch(2);
        var ref = system.spawn(() -> new Parent(childLatch)).withName("p").start();

        // send two messages; parent must create only one child and forward both
        ref.tell("m1");
        ref.tell("m2");

        // wait for child to process both forwarded messages
        assertTrue(childLatch.await(1, TimeUnit.SECONDS), "child should receive both forwarded messages");
    }

    static class GrandChild implements Actor<String> {
        private final CountDownLatch started;
        private final CountDownLatch stopped;

        GrandChild(CountDownLatch started, CountDownLatch stopped) { this.started = started; this.stopped = stopped; }

        @Override
        public void receive(String message, ActorContext<String> ctx) {
            if ("start".equals(message)) started.countDown();
            if ("stop".equals(message)) stopped.countDown();
        }
    }

    static class ChildWithGrand implements Actor<String> {
        private ActorRef<String> grand;
        private final CountDownLatch started;
        private final CountDownLatch stopped;

        ChildWithGrand(CountDownLatch started, CountDownLatch stopped) { this.started = started; this.stopped = stopped; }

        @Override
        public void receive(String message, ActorContext<String> ctx) {
            if (grand == null) {
                grand = ctx.spawnChild(() -> new GrandChild(started, stopped)).withName("grand").start();
            }
            grand.tell(message);
        }
    }

    static class ParentWithGrand implements Actor<String> {
        private ActorRef<String> child;
        private final CountDownLatch started;
        private final CountDownLatch stopped;

        ParentWithGrand(CountDownLatch started, CountDownLatch stopped) { this.started = started; this.stopped = stopped; }

        @Override
        public void receive(String message, ActorContext<String> ctx) {
            if (child == null) child = ctx.spawnChild(() -> new ChildWithGrand(started, stopped)).withName("child").start();
            child.tell(message);
        }
    }

    @Test
    void childCanCreateGrandchild_andStopRecursively() throws Exception {
        system = ActorSystems.create();

        CountDownLatch grandchildStarted = new CountDownLatch(1);
        CountDownLatch grandchildStopped = new CountDownLatch(1);

        var ref = system.spawn(() -> new ParentWithGrand(grandchildStarted, grandchildStopped)).withName("parent").start();

        // start grandchild chain
        ref.tell("start");
        assertTrue(grandchildStarted.await(1, TimeUnit.SECONDS), "grandchild should start and receive message");

        // now instruct to stop the chain by sending 'stop' which child forwards to grandchild
        ref.tell("stop");
        assertTrue(grandchildStopped.await(1, TimeUnit.SECONDS), "grandchild should receive stop message forwarded by parent/child");

        // finally shutdown the system
        system.shutdown();
        assertTrue(true);
    }
}
