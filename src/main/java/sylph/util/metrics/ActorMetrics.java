package sylph.util.metrics;

import sylph.enums.LifecycleState;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Métricas simple por actor.
 */
public class ActorMetrics {
    private final AtomicLong messagesProcessed = new AtomicLong();
    private final AtomicLong messagesRejected = new AtomicLong();
    private final Map<LifecycleState, AtomicLong> stateTransitions = new EnumMap<>(LifecycleState.class);
    private volatile LifecycleState currentState = LifecycleState.CREATED;
    private volatile Instant lastTransition = Instant.now();

    public ActorMetrics() {
        for (LifecycleState s : LifecycleState.values()) {
            stateTransitions.put(s, new AtomicLong());
        }
        stateTransitions.get(currentState).incrementAndGet();
    }

    public void incrementProcessed() {
        messagesProcessed.incrementAndGet();
    }

    public void incrementRejected() {
        messagesRejected.incrementAndGet();
    }

    public long getMessagesProcessed() { return messagesProcessed.get(); }
    public long getMessagesRejected() { return messagesRejected.get(); }

    public void setState(LifecycleState newState) {
        this.currentState = newState;
        this.lastTransition = Instant.now();
        stateTransitions.get(newState).incrementAndGet();
    }

    public LifecycleState getCurrentState() { return currentState; }
    public Instant getLastTransition() { return lastTransition; }

    public long getTransitions(LifecycleState state) {
        AtomicLong v = stateTransitions.get(state);
        return v == null ? 0L : v.get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("state=").append(currentState)
          .append(", processed=").append(getMessagesProcessed())
          .append(", rejected=").append(getMessagesRejected())
          .append(", transitions={");
        for (LifecycleState s : LifecycleState.values()) {
            sb.append(s.name()).append("=").append(getTransitions(s)).append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}
