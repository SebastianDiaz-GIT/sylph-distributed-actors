package sylph.examples;

import org.junit.jupiter.api.Test;
import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.api.ActorSystems;
import sylph.api.ActorSystem;
import sylph.enums.Supervision;
import sylph.testutils.TestProbe;

import static org.junit.jupiter.api.Assertions.*;

public class SupervisionAndLifecycleTest {

    record Msg(String s) {}

    static class FailingActor implements Actor<Msg> {
        private final TestProbe<Msg> probe;
        private int seen = 0;

        FailingActor(TestProbe<Msg> probe) {
            this.probe = probe;
        }

        @Override
        public void receive(Msg message, ActorContext<Msg> ctx) {
            probe.receiveFromActor(message);
            System.out.println("FailingActor(" + this + ") received: " + message.s + " seen=" + seen);
            if ("boom".equals(message.s)) {
                seen++;
                throw new RuntimeException("boom");
            }
            seen++;
        }
    }

    static class StatefulActor implements Actor<Msg> {
        private final TestProbe<Msg> probe;

        StatefulActor(TestProbe<Msg> probe) {
            this.probe = probe;
        }

        @Override
        public void receive(Msg message, ActorContext<Msg> ctx) {
            probe.receiveFromActor(message);
            if ("stop-self".equals(message.s)) ctx.stop();
        }
    }

    @Test
    public void supervisionRestartShouldReplaceInstanceAndProcessPending() throws InterruptedException {
        ActorSystem system = ActorSystems.create();
        try (system) {
            TestProbe<Msg> probe = new TestProbe<>();
            var ref = system.spawn(() -> new FailingActor(probe))
                    .withSupervision(Supervision.RESTART)
                    .start();

            ref.tell(new Msg("hello"));
            ref.tell(new Msg("boom"));
            ref.tell(new Msg("after-restart"));

            // Expect at least hello and after-restart processed (boom triggers restart)
            assertTrue(probe.awaitProcessedCount(2, java.time.Duration.ofSeconds(2)), "Expected at least two messages processed (hello, after-restart)");

            // Verify that after-restart was processed
            boolean seenAfter = probe.awaitProcessed(new Msg("after-restart"), java.time.Duration.ofSeconds(1));
            assertTrue(seenAfter, "Expected after-restart to be processed after RESTART supervision");
        }
    }

    @Test
    public void stopShouldDrainMailboxProcessingPendingMessagesAndRejectNewOnes() throws InterruptedException {
        ActorSystem system = ActorSystems.create();
        try (system) {
            TestProbe<Msg> probe = new TestProbe<>();
            var ref = system.spawn(() -> new StatefulActor(probe))
                    .withSupervision(Supervision.NONE)
                    .start();

            ref.tell(new Msg("m1"));
            ref.tell(new Msg("m2"));
            ref.tell(new Msg("m3"));

            // wait until first message is being processed
            assertTrue(probe.awaitProcessedCount(1, java.time.Duration.ofSeconds(2)), "Expected first message processed");

            // request external stop (should drain pending m2 and m3)
            ref.stop();

            // send message after stop — must be rejected/not processed
            ref.tell(new Msg("after-stop"));

            // wait that the drain processed m2 and m3
            assertTrue(probe.awaitProcessedCount(3, java.time.Duration.ofSeconds(2)), "Expected m1,m2,m3 processed during stop drain");

            // ensure after-stop is NOT processed
            boolean seenAfter = probe.awaitProcessed(new Msg("after-stop"), java.time.Duration.ofSeconds(1));
            assertFalse(seenAfter, "Expected after-stop NOT to be processed after stop()");
        }
    }
}

