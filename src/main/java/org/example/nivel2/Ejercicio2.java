package org.example.nivel2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ejercicio2 {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        descargarUrl(client);
    }

    private static void descargarUrl(HttpClient client) {

        HttpRequest fastReq = HttpRequest.newBuilder().uri(URI.create("https://httpbin.org/get")).build();
        HttpRequest mediumReq = HttpRequest.newBuilder().uri(URI.create("https://jsonplaceholder.typicode.com/posts/1")).build();
        HttpRequest slowReq = HttpRequest.newBuilder().uri(URI.create("https://httpbin.org/delay/2")).build();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            submitRequests(executor, client, fastReq, 5000);
            submitRequests(executor, client, mediumReq, 3000);
            submitRequests(executor, client, slowReq, 2000);

        }
    }

    private static void submitRequests(
            ExecutorService executor,
            HttpClient client,
            HttpRequest request,
            int count
    ) {
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try {
                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Por performance, mejor NO imprimir cada respuesta (solo primeras 3)
                    // System.out.println(response.body());

                } catch (IOException | InterruptedException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
        }
    }
}
