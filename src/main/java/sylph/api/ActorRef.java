package sylph.api;

/**
 * Referencia pública a un actor en SYLPH.
 *
 * <p>Representa la forma segura y tipada de enviar mensajes a un actor.
 * La implementación concreta queda oculta al usuario; la API sólo expone
 * operaciones de alto nivel: enviar un mensaje (fire-and-forget) y solicitar
 * la detención del actor.
 *
 * <p>Semántica:
 * <ul>
 *   <li>{@link #tell(Object)} encola un mensaje inmutable en la mailbox del actor.
 *       No garantiza entrega (no fiable por diseño a menos que el usuario añada ACKs).</li>
 *   <li>{@link #stop()} solicita la detención externa del actor de forma inmediata.
 *       Es una orden externa (forzada) y no pasa por el método {@code receive} del actor.
 * </ul>
 *
 * <p>Ejemplo de uso:
 * <pre>
 * ActorRef<String> ref = system.spawn("printer", PrinterActor::new);
 * ref.tell("hola");
 * ref.stop(); // detiene el actor externamente
 * </pre>
 *
 * @param <M> tipo de mensaje manejado por el actor
 */
public interface ActorRef<M> {
    /**
     * Envia (fire-and-forget) un mensaje al actor.
     * El mensaje debe ser inmutable.
     *
     * @param message mensaje a enviar
     */
    void tell(M message);

    /**
     * Solicita que el actor se detenga. Esta operación es externa y puede interrumpir
     * el procesamiento actual del actor. Si el actor necesita realizar limpieza se
     * recomienda enviar un mensaje de control y que el propio actor llame a {@code ctx.stop()}.
     */
    void stop();
}
