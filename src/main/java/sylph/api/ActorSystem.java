package sylph.api;

import sylph.enums.MailboxType;

import java.util.function.Supplier;

/**
 * Sistema de actores público de SYLPH — abstracción mínima para crear y
 * gestionar el ciclo de vida de actores.
 */
public interface ActorSystem extends AutoCloseable {

    /**
     * Comienza un builder para crear un actor usando opciones encadenadas.
     */
    <M> SpawnBuilder<M> spawn(Supplier<Actor<M>> actorSupplier);

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
     */
    void shutdown();

    @Override
    default void close() {
        shutdown();
    }

}