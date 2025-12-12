package sylph.actors.records;

import sylph.interfaces.message.Message;

public record PriorityMessage(int priority, String content) implements Message, Comparable<PriorityMessage> {

    @Override
    public int compareTo(PriorityMessage o) {
        // Menor valor = mayor prioridad
        return Integer.compare(this.priority, o.priority);
    }

    @Override
    public String toString() {
        return "[priority=" + priority + ", content='" + content + "']";
    }
}

