package org.example.nivel1;

import java.util.concurrent.Executors;

public class Ejercicio2 {
    /*
    * Realizar una pausa en cada hilo virtual durante un tiempo (1000 ms)
    * */

    public static void main(String[] args) throws InterruptedException {
        versionExecutor();
    }

    private static void versionSensilla() throws InterruptedException {
        int total = 1000;
        Thread[] threads = new Thread[total];

        for (int i = 0; i < total; i++) {
            int id = i;

            threads[i] = Thread.ofVirtual().start(() -> {
                System.out.println("Hilo virtual " + id + " durmiendo 1000ms");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Hilo virtual " + id + " despierto");
            });
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    /*
    * Esta es la version usando un Executor de hilos virtuales
    * ideal para proyectos grandes o cuando se quiere gestionar mejor los hilos
    * */
    private static void versionExecutor() throws InterruptedException {
        int total = 1000;

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < total; i++) {
                int id = i;

                executor.submit(() -> {
                    System.out.println("Hilo virtual " + id + " durmiendo 1000ms");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Hilo virtual " + id + " despierto");
                });
            }
        }
    }
}
