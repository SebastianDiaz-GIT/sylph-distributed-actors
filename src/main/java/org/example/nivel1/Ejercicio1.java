package org.example.nivel1;

public class Ejercicio1 {
    /*
    * Para este ejercicio el objetivo es crear 1000 hilos virtuales que impriman su
    * identificador (del 1 al 1000) o un mensaje aleatorio.
    * */
    public static void main(String[] args) throws InterruptedException {
        //Simple, pero si no se crea la referencia al hilo y el main acaba antes, no se ven los resultados.
        Thread[] threads = new Thread[1000];
        for (int i = 1; i <= 1000; i++) {
            int id = i;
            threads[i - 1] = Thread.ofVirtual().start(() -> {
                System.out.println("Hilo virtual: id = " + id);
            });
        }
        // Esperar a que todos los hilos terminen
        for (Thread t : threads) {
            t.join();
        }
    }
}
