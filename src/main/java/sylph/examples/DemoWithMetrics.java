package sylph.examples;

import sylph.actors.ActorSystemImpl;
import sylph.actors.BasicActorImpl;
import sylph.interfaces.message.Message;
import sylph.util.metrics.MetricsHttpExporter;

public class DemoWithMetrics {
    public static final class SimpleMsg implements Message {
        private final String text;
        public SimpleMsg(String text) { this.text = text; }
        public String toString() { return text; }
    }

    public static final class SimpleActor extends BasicActorImpl {
        @Override
        protected void onReceive(Message message) {
            System.out.println("SimpleActor received: " + message);
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystemImpl system = new ActorSystemImpl();
        SimpleActor actor = new SimpleActor();
        var ref = system.actorOf("simple", actor);

        // Send some messages
        ref.send(new SimpleMsg("one"));
        ref.send(new SimpleMsg("two"));
        ref.send(new SimpleMsg("three"));

        // Start HTTP metrics exporter
        MetricsHttpExporter exporter = new MetricsHttpExporter(system, 8000);
        exporter.start();
        System.out.println("Metrics available at http://localhost:8000/metrics");

        // Sleep briefly to allow processing (in a real test use sync mechanisms)
        Thread.sleep(500);

        // Show metrics in stdout and stop
        system.logAllMetrics();
        system.stopAll();
        exporter.stop(0);
    }
}

