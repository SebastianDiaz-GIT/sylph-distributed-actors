package sylph.testhooks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ExceptionNotifierRegistry {
    private ExceptionNotifierRegistry() {}

    public interface ExceptionListener<M> {
        void onActorException(Object publicRef, M message, Throwable t);
    }

    private static final List<ExceptionListener<?>> listeners = new CopyOnWriteArrayList<>();

    public static <M> void registerExceptionListener(ExceptionListener<M> listener) {
        listeners.add(listener);
    }

    public static <M> void unregisterExceptionListener(ExceptionListener<M> listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static <M> void notifyException(Object publicRef, M message, Throwable t) {
        for (ExceptionListener l : listeners) {
            try {
                l.onActorException(publicRef, message, t);
            } catch (Throwable ignore) {
            }
        }
    }
}
