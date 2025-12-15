package sylph.api;

/**
 * Interfaz pública que representa un actor en el runtime SYLPH.
 *
 * <p>Contrato principal:
 * <ul>
 *   <li>Un actor procesa un mensaje a la vez (secuencialmente).</li>
 *   <li>Los mensajes deben ser inmutables (usar {@code record} o tipos inmutables).</li>
 *   <li>No se deben exponer hilos, ejecutores ni futuros en la API pública.</li>
 * </ul>
 *
 * <p>El método {@link #receive(Object, ActorContext)} es invocado por el runtime
 * cuando llega un nuevo mensaje. El actor puede usar el contexto para obtener
 * su propia referencia {@code self()} o solicitar su detención con {@code stop()}.
 *
 * @param <M> tipo de mensaje manejado por este actor
 *
 * <h3>Ejemplo</h3>
 * <pre>
 * public class PrinterActor implements Actor<String> {
 *     public void receive(String message, ActorContext<String> ctx) {
 *         System.out.println("received: " + message);
 *         if ("stop".equals(message)) ctx.stop();
 *     }
 * }
 * </pre>
 */
public interface Actor<M> {
    void receive(M message, ActorContext<M> ctx) throws Exception;
}
