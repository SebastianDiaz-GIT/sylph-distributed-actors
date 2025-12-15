package sylph.examples;

import sylph.actors.*;
import sylph.actors.records.IncrementMessage;
import sylph.interfaces.message.Message;
import sylph.mailbox.FifoMailbox;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Ejemplo demostrando backpressure natural con FifoMailbox de capacidad limitada.
 * Puede ejecutarse en cualquier proyecto que use el framework SYLPH.
 */
public class FifoMailboxBackpressureDemo {
    public static void main(String[] args) throws InterruptedException {
        ActorSystemImpl system = new ActorSystemImpl();
        List<String> processed = Collections.synchronizedList(new ArrayList<>());

        // Actor FIFO con retardo artificial para simular procesamiento lento
        ActorRefImpl fifoTest = system.actorOf("fifoTest", new BasicActorImpl(new FifoMailbox(3)) {
            @Override
            protected void onReceive(Message message) {
                try {
                    Thread.sleep(300); // Simula procesamiento lento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                processed.add(message.toString());
                System.out.println("Procesado: " + message);
            }
        });

        // Enviar mensajes rápidamente desde un hilo aparte
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 7; i++) {
                try {
                    System.out.println("Enviando: " + i);
                    fifoTest.send(new IncrementMessage(i));
                    System.out.println("Enviado: " + i);
                } catch (Exception e) {
                    System.out.println("Error al enviar: " + i);
                }
            }
        });
        producer.start();
        producer.join();

        // Esperar a que se procesen todos los mensajes
        Thread.sleep(2500);

        // Imprimir el orden de procesamiento
        System.out.println("\nOrden de procesamiento FIFO:");
        for (String msg : processed) {
            System.out.println(msg);
        }

        system.stopAll();
    }
}

