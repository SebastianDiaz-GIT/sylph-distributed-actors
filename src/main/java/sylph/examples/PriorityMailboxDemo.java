package sylph.examples;

import sylph.actors.*;
import sylph.actors.records.PriorityMessage;
import sylph.mailbox.PriorityMailbox;
import sylph.interfaces.message.Message;

/**
 * Ejemplo de uso de PriorityMailbox con PriorityMessage.
 */
public class PriorityMailboxDemo {
    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = new ActorSystem();

        // Actor que imprime los mensajes en orden de prioridad
        ActorRef actor = system.actorOf("priorityActor", new BasicActor(new PriorityMailbox()) {
            @Override
            protected void onReceive(Message message) {
                if (message instanceof PriorityMessage pm) {
                    System.out.println("Procesado: " + pm);
                } else {
                    System.out.println("Mensaje desconocido: " + message);
                }
            }
        });

        // Enviar mensajes con diferentes prioridades
        actor.send(new PriorityMessage(10, "Baja prioridad"));
        actor.send(new PriorityMessage(1, "Alta prioridad"));
        actor.send(new PriorityMessage(5, "Prioridad media"));
        actor.send(new PriorityMessage(3, "Prioridad intermedia"));

        // Esperar a que se procesen los mensajes
        Thread.sleep(1000);
        system.stopAll();
    }
}

