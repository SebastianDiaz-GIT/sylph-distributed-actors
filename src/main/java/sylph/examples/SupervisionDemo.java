package sylph.examples;

import sylph.api.Actor;
import sylph.api.ActorContext;
import sylph.api.ActorRef;
import sylph.api.ActorSystem;
import sylph.api.ActorSystems;
import sylph.enums.Supervision;
import sylph.enums.MailboxType;

public class SupervisionDemo {
    public static class FailingActor implements Actor<String> {
        private int seen = 0;
        @Override
        public void receive(String message, ActorContext<String> ctx) throws Exception {
            System.out.println("FailingActor(" + this + ") received: " + message + " seen=" + seen);
            if ("boom".equals(message) && seen == 0) {
                seen++;
                throw new RuntimeException("boom!");
            }
            System.out.println("Processed by " + this + ": " + message);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("--- Supervision Demo START ---");
        try (ActorSystem sys = ActorSystems.create()) {
            // RESTART policy: actor instance will be recreated after failure
            ActorRef<String> r1 = sys.spawn(FailingActor::new)
                    .withName("nonFailingActor")
                    .withSupervision(Supervision.RESTART)
                    .start();
            r1.tell("hello");

            // Using builder to set supervision
            var refRestart = sys.spawn(FailingActor::new)
                    .withName("restartActor")
                    .withSupervision(Supervision.RESTART)
                    .start();

            refRestart.tell("boom");
            refRestart.tell("after-restart");

            var refStop = sys.spawn(FailingActor::new)
                    .withName("stopActor")
                    .withSupervision(Supervision.STOP)
                    .start();

            refStop.tell("boom");
            refStop.tell("will-be-rejected");

            // Small sleep to allow processing in demo (not ideal for unit tests)
            Thread.sleep(500);
        }
        System.out.println("--- Supervision Demo END ---");
    }
}

