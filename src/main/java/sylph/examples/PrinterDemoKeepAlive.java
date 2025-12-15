package sylph.examples;

import sylph.api.ActorRef;
import sylph.api.ActorSystems;

import java.io.IOException;

/**
 * Demo que mantiene el ActorSystem vivo hasta que el usuario presione ENTER.
 * Útil para ver que los actores permanecen activos después de procesar mensajes.
 */
public class PrinterDemoKeepAlive {
    public static void main(String[] args) throws IOException {
        // Crear el sistema (no en try-with-resources para evitar cierre automático)
        var system = ActorSystems.create();

        // Spawn de actores con nombre legible
        ActorRef<String> printer = system.spawn("printer", PrinterActor::new);
        ActorRef<String> printer2 = system.spawn("printer2", PrinterActor::new);

        // Enviar algunos mensajes
        printer.tell("hello");
        printer.tell("world");
        printer2.tell("foo");

        System.out.println("Actores iniciados. Pulsa ENTER para detener el sistema...");

        // Esperar a que el usuario presione ENTER; durante este tiempo los actores siguen vivos
        System.in.read();

        // Cerrar el sistema de forma ordenada
        system.shutdown();
        System.out.println("Sistema detenido. Saliendo.");
    }
}

