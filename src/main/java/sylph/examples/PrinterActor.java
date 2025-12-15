package sylph.examples;

import sylph.api.Actor;
import sylph.api.ActorContext;

/**
 * Actor de ejemplo que imprime mensajes de texto y se detiene al recibir "stop".
 */
public class PrinterActor implements Actor<String> {
    @Override
    public void receive(String message, ActorContext<String> ctx) {
        System.out.println("PrinterActor received: " + message);
        if ("stop".equals(message)) {
            ctx.stop();
        }
    }
}

