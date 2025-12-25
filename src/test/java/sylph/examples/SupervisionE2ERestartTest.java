package sylph.examples;

import org.junit.jupiter.api.Test;
import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.api.ActorRef;
import sylph.api.ActorSystems;
import sylph.api.ActorSystem;
import sylph.enums.Supervision;
import sylph.testutils.TestProbe;

import static org.junit.jupiter.api.Assertions.*;

public class SupervisionE2ERestartTest {

    record Msg(String s) {}

    static class FailingActor implements Actor<Msg> {
        private final ActorRef<Msg> probeRef;
        private int seen = 0;

        FailingActor(ActorRef<Msg> probeRef) {
            this.probeRef = probeRef;
        }

        @Override
        public void receive(Msg message, ActorContext<Msg> ctx) {
            // Forward processed message to the probe actor via ActorRef (end-to-end)
            probeRef.tell(message);
            if ("boom".equals(message.s)) {
                seen++;
                throw new RuntimeException("boom");
            }
            seen++;
        }
    }

    @Test
    public void restartPolicyShouldProcessPendingMessagesEndToEnd() throws InterruptedException {
        ActorSystem system = ActorSystems.create();
        try (system) {
            // Create a TestProbe helper and a probe actor that forwards to it
            try (TestProbe<Msg> probe = new TestProbe<>()) {
                ActorRef<Msg> probeRef = system.spawn(() -> new sylph.testutils.ActorProbeActor<>(probe)).start();

                // Spawn the failing actor that will forward processed messages to the probe actor
                ActorRef<Msg> target = system.spawn(() -> new FailingActor(probeRef))
                        .withSupervision(Supervision.RESTART)
                        .start();

                target.tell(new Msg("hello"));
                target.tell(new Msg("boom"));
                target.tell(new Msg("after-restart"));

                // Expect at least two messages delivered to the probe: hello and after-restart
                assertTrue(probe.awaitProcessedCount(2, java.time.Duration.ofSeconds(2)), "Probe should receive hello and after-restart after restart");

                boolean sawAfter = probe.awaitProcessed(new Msg("after-restart"), java.time.Duration.ofSeconds(1));
                assertTrue(sawAfter, "Expected after-restart to be received by probe end-to-end");
            }
        }
    }
}
