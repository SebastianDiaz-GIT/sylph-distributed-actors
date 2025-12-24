package sylph.examples;

import sylph.actors.records.PriorityMessage;
import sylph.api.ActorRef;
import sylph.api.ActorSystems;
import sylph.enums.MailboxType;
import sylph.examples.printers.PrinterActor;
import sylph.examples.printers.PriorityPrinter;

public class PrinterDemo {
    public static void main(String[] args) throws InterruptedException {
        // Usamos la fábrica pública para obtener un ActorSystem (devuelve la interfaz ActorSystem)
        try (var system = ActorSystems.create()) {
            // Creamos un actor con nombre legible "printer"
            ActorRef<PriorityMessage> printer = system.spawn(PriorityPrinter::new, MailboxType.PRIORITY);
            ActorRef<String> printer2 = system.spawn("printer2", PrinterActor::new);

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
