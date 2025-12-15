package sylph.api;

import java.util.function.Supplier;

/**
 * Sistema de actores público de SYLPH — abstracción mínima para crear y
 * gestionar el ciclo de vida de actores.
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Proveer métodos para crear actores (spawn).</li>
 *   <li>Permitir apagar el sistema y liberar recursos (shutdown / close).</li>
 * </ul>
 *
 * <p>Contrato y garantías:
 * <ul>
 *   <li>Las implementaciones deben ser thread-safe para crear/registrar actores.</li>
 *   <li>La interfaz pública no expone detalles de concurrencia (hilos, executors).</li>
 *   <li>El método {@link #close()} delega por defecto en {@link #shutdown()} para
 *       permitir uso con try-with-resources.</li>
 * </ul>
 *
 * <p>Notas sobre nombres y spawn:
 * <ul>
 *   <li>{@code spawn(Supplier<Actor<M>>)} crea un actor con un nombre generado.</li>
 *   <li>{@code spawn(String, Supplier<Actor<M>>)} crea un actor con el nombre
 *       proporcionado por el usuario; la implementación puede rechazar nombres duplicados.</li>
 *   <li>{@code spawn(String, Supplier<Actor<M>>, MailboxType)} crea un actor con el tipo
 *       de mailbox especificado por el usuario.</li>
 * </ul>
 *
 * <h3>Ejemplo</h3>
 * <pre>
 * try (ActorSystem system = ActorSystems.create()) {
 *     ActorRef<String> ref = system.spawn("printer", PrinterActor::new);
 *     ref.tell("hola");
 * }
 * </pre>
 */
public interface ActorSystem extends AutoCloseable {

    /**
     * Crea un actor con un nombre generado automáticamente.
     */
    <M> ActorRef<M> spawn(Supplier<Actor<M>> actorSupplier);
    /**
     * Crea un actor con un nombre generado automáticamente y el tipo de mailbox especificado.
     */
    <M> ActorRef<M> spawn(Supplier<Actor<M>> actorSupplier, MailboxType mailboxType);

    /**
     * Crea un actor con el nombre proporcionado. El nombre debe ser único en el sistema
     * o la implementación debe decidir la política en caso de conflicto (por ejemplo, lanzar
     * una excepción o devolver la referencia ya existente).
     */
    <M> ActorRef<M> spawn(String name, Supplier<Actor<M>> actorSupplier);

    /**
     * Crea un actor con el nombre y tipo de mailbox especificados.
     */
    <M> ActorRef<M> spawn(String name, Supplier<Actor<M>> actorSupplier, MailboxType mailboxType);

    /**
     * Apaga el sistema liberando recursos y deteniendo todos los actores.
     * Implementaciones concretas deben documentar si el apagado es inmediato
     * o si espera a que las mailboxes se vacíen.
     */
    void shutdown();

    @Override
    default void close() {
        shutdown();
    }

}