package sylph.examples.printers;

import sylph.actors.records.PriorityMessage;
import sylph.api.Actor;
import sylph.api.ActorContext;

public class PriorityPrinter implements Actor<PriorityMessage> {
    @Override
    public void receive(PriorityMessage message, ActorContext<PriorityMessage> ctx) {
        System.out.println("PrinterActor received: " + message);
        if ("stop".equals(message.content())) {
            ctx.stop();
        }
    }
}
