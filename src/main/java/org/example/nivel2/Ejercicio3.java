package org.example.nivel2;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio3 {
    /*
    * 1. Simulación de tráfico como sistema distribuido simplificado
    * 500 vehículos (virtual threads)
    * Cada vehículo:
    *   Se mueve 1 paso cada 100–300 ms
    *   De vez en cuando entra a una sección crítica (un “puente”)
    *   Usa locks para no chocar
    *   ✔️ Modelo de actores supersimple usando Virtual Threads.
    * */
    public static void main(String[] args) throws InterruptedException {
        final int VEHICULOS = 500;
        final int PASOS = 100;
        Lock puente = new ReentrantLock();
        Thread[] threads = new Thread[VEHICULOS];
        Random random = new Random();

        for (int v = 0; v < VEHICULOS; v++) {
            int id = v + 1;
            threads[v] = Thread.ofVirtual().start(() -> {
                for (int paso = 1; paso <= PASOS; paso++) {
                    try {
                        // Simula movimiento
                        Thread.sleep(100 + random.nextInt(201)); // 100-300 ms
                        // Cada 10 pasos intenta cruzar el puente
                        if (paso % 10 == 0) {
                            puente.lock();
                            try {
                                System.out.println("Vehículo " + id + " cruza el puente en el paso " + paso);
                                Thread.sleep(100 + random.nextInt(101)); // Cruza el puente (100-200 ms)
                            } finally {
                                puente.unlock();
                            }
                        } else {
                            System.out.println("Vehículo " + id + " avanza al paso " + paso);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        // Esperar a que todos los vehículos terminen
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Simulación terminada.");
    }
}
