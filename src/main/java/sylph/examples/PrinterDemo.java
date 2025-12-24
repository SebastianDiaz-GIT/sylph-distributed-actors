package sylph.examples;

import sylph.actors.records.PriorityMessage;
import sylph.api.ActorRef;
import sylph.api.ActorSystems;
import sylph.enums.MailboxType;
import sylph.enums.Supervision;
import sylph.examples.printers.PrinterActor;
import sylph.examples.printers.PriorityPrinter;

public class PrinterDemo {
    public static void main(String[] args) throws InterruptedException {
        // Usamos la fábrica pública para obtener un ActorSystem (devuelve la interfaz ActorSystem)
        try (var system = ActorSystems.create()) {
            // Creamos un actor con nombre legible "printer" usando el builder fluent
            ActorRef<PriorityMessage> printer = system.spawn(PriorityPrinter::new)
                    .withName("printer")
                    .withMailbox(MailboxType.PRIORITY)
                    .withSupervision(Supervision.NONE)
                    .start();

            ActorRef<String> printer2 = system.spawn(PrinterActor::new)
                    .withName("printer2")
                    .start();

            // Enviamos mensajes al actor
            printer.tell(new PriorityMessage(1, "low priority"));
            printer.tell(new PriorityMessage(5, "medium priority"));
            printer.tell(new PriorityMessage(8, "high priority"));
            printer.tell(new PriorityMessage(2, "low priority"));
            printer2.tell("foo");

            // Esperamos un poco para que procese los mensajes
            Thread.sleep(200);

            // Detenemos el actor desde la API pública
            printer.stop();

            // Espera breve antes de cerrar el sistema
            Thread.sleep(50);
        }
    }
}
