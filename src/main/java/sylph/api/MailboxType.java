package sylph.api;

/**
 * Tipos de mailbox disponibles en SYLPH (v0.1).
 *
 * <p>Este enum permite seleccionar una política de mailbox al crear un actor
 * sin exponer las clases de implementación del mailbox en la API pública.
 */
public enum MailboxType {
    FIFO,
    PRIORITY
}

