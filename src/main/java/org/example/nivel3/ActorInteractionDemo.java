package org.example.nivel3;

/**
 * Ejercicio creacion de 2 actores que se comuniquen entre ellos
 * El actor principal escucha lo que le dice el actor secundario
 */

// Mensaje de texto
record TextMessage(String text) implements Message {
}

// Mensaje de respuesta
record ResponseMessage(String text) implements Message {
}

public class ActorInteractionDemo {
    public static void main(String[] args) throws InterruptedException {
        // Actor principal que imprime los mensajes que recibe
        BasicActor mainActor = new BasicActor() {
            @Override
            protected void onReceive(Message message) {
                if (message instanceof ResponseMessage(String text)) {
                    System.out.println("MainActor recibió respuesta: " + text);
                } else {
                    System.out.println("MainActor recibió mensaje desconocido: " + message);
                }
            }
        };

        // Actor que responde a los mensajes
        BasicActor echoActor = getEchoActor(mainActor);

        // Esperamos para que los actores procesen los mensajes
        Thread.sleep(1000);
        echoActor.stop();
        mainActor.stop();
    }

    private static BasicActor getEchoActor(BasicActor mainActor) {
        BasicActor echoActor = new BasicActor() {
            @Override
            protected void onReceive(Message message) {
                if (message instanceof TextMessage(String text)) {
                    // Responde al mainActor
                    mainActor.send(new ResponseMessage("EchoActor responde: " + text));
                }
            }
        };

        // Enviamos mensajes al EchoActor
        echoActor.send(new TextMessage("Hola EchoActor!"));
        echoActor.send(new TextMessage("¿Puedes responder?"));
        echoActor.send(new TextMessage("Gracias!"));
        return echoActor;
    }
}
