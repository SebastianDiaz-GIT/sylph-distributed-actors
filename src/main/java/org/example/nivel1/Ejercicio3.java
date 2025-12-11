package org.example.nivel1;

import java.util.concurrent.Executors;

public class Ejercicio3 {

    /*
    * Crear un ejecutor de hilos virtuales
    * enviarle 100 tareas que devuelvan un número aleatorio
    * */

    public static void main(String[] args) {
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()){
            for(int i = 0; i < 100; i++){
                executor.submit(() -> {
                    int randomNum = (int)(Math.random() * 1000);
                    System.out.println("Número aleatorio: " + randomNum);
                    return randomNum;
                });
            }
        }
    }
}
