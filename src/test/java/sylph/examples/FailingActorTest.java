package sylph.examples;

import org.junit.jupiter.api.Test;
import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.api.ActorSystems;
import sylph.api.ActorSystem;
import sylph.enums.Supervision;
import sylph.testutils.TestProbe;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class FailingActorTest {

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
            if ("stop".equals(message.s)) ctx.stop();
        }
    }

    @Test
    public void supervisionStopShouldRejectFollowingMessages() throws InterruptedException {
        ActorSystem system = ActorSystems.create();
        try (system) {
            TestProbe<Msg> probe = new TestProbe<>();
            // spawn actor with STOP supervision using the SpawnBuilder API
            var ref = system.spawn(() -> new FailingActor(probe))
                    .withSupervision(Supervision.STOP)
                    .start();

            ref.tell(new Msg("hello"));
            ref.tell(new Msg("boom"));
            ref.tell(new Msg("will-be-rejected"));

            // wait a bit for processing
            assertTrue(probe.awaitProcessedCount(2, Duration.ofSeconds(2)), "Expected two messages processed (hello, boom)");

            // ensure third is NOT processed
            boolean seenThird = probe.awaitProcessed(new Msg("will-be-rejected"), Duration.ofSeconds(1));
            assertFalse(seenThird, "Expected will-be-rejected NOT to be processed after supervision STOP");
        }
    }
}
