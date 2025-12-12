package org.example.nivel3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Mensaje para incrementar el contador
record IncrementMessage(int amount) implements Message {
}

// Mensaje para imprimir el estado
class PrintStateMessage implements Message {}

public class BasicActor {
    private final BlockingQueue<Message> mailbox = new LinkedBlockingQueue<>();
    private final Thread actorThread;
    private volatile boolean running = true;
    // Estado del actor (ejemplo: contador)
    private int counter = 0;

    public BasicActor() {
        // El actor procesa mensajes en un hilo virtual
        actorThread = Thread.ofVirtual().start(() -> {
            while (running) {
                try {
                    Message message = mailbox.take();
                    onReceive(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    // Función para enviar mensajes al actor
    public void send(Message message) {
        mailbox.offer(message);
    }

    // Lógica de procesamiento de mensajes
    protected void onReceive(Message message) {
        if (message instanceof IncrementMessage(int amount)) {
            counter += amount;
        } else if (message instanceof PrintStateMessage) {
            System.out.println("Estado actual del actor: " + counter);
        } else {
            System.out.println("Mensaje desconocido: " + message);
        }
    }

    // Función para detener el actor
    public void stop() {
        running = false;
        actorThread.interrupt();
    }
}
