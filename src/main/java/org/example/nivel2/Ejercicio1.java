package org.example.nivel2;

import java.util.concurrent.Executors;

public class Ejercicio1 {
    /*
    * Lanzar 10000 hilos virtuales que simulen peticiones http
    * */

    public static void main(String[] args) {
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()){
            for (int i = 0; i < 10000; i++) {
                int id = i;
                executor.submit(() -> {
                    System.out.println("Simulando petición HTTP en hilo virtual: " + id);
                    // Simulación de procesamiento de la petición
                    try {
                        Thread.sleep((long)(Math.random() * 500)); // Simula tiempo de respuesta variable
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Petición HTTP procesada en hilo virtual: " + id);
                });
            }
        }
    }
}
