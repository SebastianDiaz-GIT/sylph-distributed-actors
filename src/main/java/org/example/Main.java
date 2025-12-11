package org.example;

import java.util.concurrent.Executors;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {

        // Ejemplo básico: crear 10 virtual threads directamente

        for (int i = 1; i <= 10; i++) {
            int id = i;
            Thread.ofVirtual().start(() -> {
                System.out.println("Virtual thread directo: i = " + id);
            });
        }

        // Ejemplo usando un Executor con virtual threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                int id = i;
                executor.submit(() -> {
                    System.out.println("Virtual thread con Executor: tarea " + id);
                });
            }
            executor.submit(() ->{
                System.out.println("Virtual thread con Executor: tarea bloqueante 1");
                try {
                    sleep(10_000);
                    System.out.println("Virtual thread con Executor: tarea bloqueante 1 finalizada");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}