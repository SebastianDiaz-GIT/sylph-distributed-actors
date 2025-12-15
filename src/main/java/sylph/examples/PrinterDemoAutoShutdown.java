package sylph.examples;

import sylph.api.ActorRef;
import sylph.api.ActorSystems;

/**
 * Demo que mantiene actores activos durante un tiempo (sleep) y luego
 * realiza un shutdown programático del sistema para demostrar que los
 * actores permanecen vivos mientras el sistema no se cierre.
 */
public class PrinterDemoAutoShutdown {
    public static void main(String[] args) throws InterruptedException {
        var system = ActorSystems.create();

        ActorRef<String> p1 = system.spawn("printerA", PrinterActor::new);
        ActorRef<String> p2 = system.spawn("printerB", PrinterActor::new);

        p1.tell("msg1");
        p2.tell("msg2");

        // Mantener el sistema vivo durante 1 segundo
        Thread.sleep(1000);

        p1.tell("after-sleep");

        // Esperar un poco más para ver los mensajes procesados
        Thread.sleep(500);

        // Shutdown programático: esto detiene todos los actores
        System.out.println("Shutting down system now...");
        system.shutdown();

        // Espera breve para que el shutdown complete
        Thread.sleep(100);
        System.out.println("Done.");
    }
}

