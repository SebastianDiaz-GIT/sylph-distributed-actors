package sylph.api;

/**
 * Fábrica de sistemas de actores.
 *
 * <p>Proporciona métodos de conveniencia para obtener una implementación
 * por defecto de {@link ActorSystem}. Mantener una fábrica permite cambiar
 * la implementación por defecto sin romper el código del usuario.
 *
 * <h3>Uso</h3>
 * <pre>
 * ActorSystem system = ActorSystems.create();
 * try (system) {
 *     var ref = system.spawn("printer", PrinterActor::new);
 *     ref.tell("hola");
 * }
 * </pre>
 */
public final class ActorSystems {
    private ActorSystems() {}

    /**
     * Crea un ActorSystem local por defecto.
     * @return instancia de {@link ActorSystem}
     */
    public static ActorSystem create() {
        return DefaultActorSystem.create();
    }

    /**
     * Alias explícito para crear un sistema local.
     * Útil para futuras extensiones (por ejemplo: createCluster(), createRemote()).
     */
    public static ActorSystem createLocal() {
        return create();
    }
}
