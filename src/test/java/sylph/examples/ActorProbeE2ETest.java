package sylph.examples;

import org.junit.jupiter.api.Test;
import sylph.api.ActorSystems;
import sylph.api.ActorSystem;
import sylph.testutils.TestProbe;
import sylph.testutils.ActorProbeActor;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ActorProbeE2ETest {
    record Msg(String s) {}

    @Test
    public void probeActorShouldReceiveMessagesViaActorSystem() throws InterruptedException {
        ActorSystem system = ActorSystems.create();
        try (system) {
            TestProbe<Msg> probe = new TestProbe<>();
            var probeRef = system.spawn(() -> new ActorProbeActor<>(probe)).start();

            // send messages to the probe actor via ActorRef
            probeRef.tell(new Msg("one"));
            probeRef.tell(new Msg("two"));

            // ensure probe actor processed them (end-to-end)
            assertTrue(probe.awaitProcessedCount(2, Duration.ofSeconds(2)), "Probe actor should see two messages via ActorSystem");
        }
    }
}

