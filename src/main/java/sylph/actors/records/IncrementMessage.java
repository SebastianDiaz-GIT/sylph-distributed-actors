package sylph.actors.records;

import sylph.interfaces.message.Message;

/**
 * Ejemplo de mensaje para incrementar un contador en el actor.
 */
public record IncrementMessage(int amount) implements Message {
}

