package sylph.api.internal;

import sylph.interfaces.message.Message;

/**
 * Mensaje interno que envuelve un payload de la API pública.
 *
 * Para soportar mailboxes de prioridad (PriorityBlockingQueue) esta clase
 * implementa Comparable y delega la comparación al payload si éste
 * implementa Comparable.
 */
public final class ApiMessage<M> implements Message, Comparable<ApiMessage<?>> {
    private final M payload;

    public ApiMessage(M payload) {
        this.payload = payload;
    }

    public M payload() {
        return payload;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public int compareTo(ApiMessage<?> o) {
        if (o == this) return 0;
        if (this.payload == null && o.payload == null) return 0;
        if (this.payload == null) return -1;
        if (o.payload == null) return 1;

        // Si ambos payloads son Comparable, delegamos la comparación.
        if (this.payload instanceof Comparable && o.payload instanceof Comparable) {
            try {
                Comparable a = (Comparable) this.payload;
                return a.compareTo(o.payload);
            } catch (ClassCastException e) {
                // Tipos no comparables entre sí -> fallback a 0
                return 0;
            }
        }
        // Si no son comparables, mantener orden indeterminado (fallback)
        return 0;
    }
}
