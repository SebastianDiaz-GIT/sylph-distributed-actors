package sylph.util.metrics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sylph.actors.ActorRefImpl;
import sylph.actors.ActorSystemImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Exportador HTTP muy simple que expone las métricas de ActorSystemImpl en /metrics.
 * No agrega dependencias externas; utiliza la HttpServer incluida en la JDK.
 */
public class MetricsHttpExporter {
    private final HttpServer server;

    public MetricsHttpExporter(ActorSystemImpl system, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/metrics", new MetricsHandler(system));
        this.server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    public void start() {
        server.start();
    }

    public void stop(int delaySeconds) {
        server.stop(delaySeconds);
    }

    static class MetricsHandler implements HttpHandler {
        private final ActorSystemImpl system;

        MetricsHandler(ActorSystemImpl system) {
            this.system = system;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, ActorMetrics> all = system.getAllMetrics();
            StringBuilder sb = new StringBuilder();
            all.forEach((k, v) -> sb.append(k).append(" ").append(v.toString()).append("\n"));
            byte[] bytes = sb.toString().getBytes();
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}

