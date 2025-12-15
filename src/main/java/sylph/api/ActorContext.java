package sylph.api;

/**
 * Contexto provisto al actor por el runtime.
 *
 * <p>El contexto permite al actor interactuar con el sistema sin exponer
 * detalles internos de concurrencia. Las operaciones disponibles son mínimas
 * por diseño para mantener un API sencilla y segura.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Proveer la referencia pública {@link #self()} del propio actor.</li>
 *   <li>Permitir solicitar la detención del actor mediante {@link #stop()}.</li>
 * </ul>
 *
 * <p>Notas:
 * <ul>
 *   <li>El contexto no expone la referencia al remitente en esta versión (v0.1).
 *       En versiones futuras se podrá ampliar (p. ej. {@code sender()}).</li>
 *   <li>Llamar a {@code stop()} desde el actor indica que este actor desea
 *       terminar su ejecución. El runtime realizará la parada correspondiente.</li>
 * </ul>
 *
 * @param <M> tipo de mensaje manejado por el actor
 */
public interface ActorContext<M> {
    /**
     * Obtiene la referencia pública del actor actual (tipo seguro y tipado).
     */
    ActorRef<M> self();

    /**
     * Solicita la detención del actor. Debe ser llamada desde el propio actor
     * cuando éste desee terminar su ejecución de forma controlada.
     */
    void stop();
}
