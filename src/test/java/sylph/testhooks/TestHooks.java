package sylph.testhooks;

/**
 * Test-only shim for TestHooks. Delegates to production ExceptionNotifierRegistry.
 * This file lives under src/test so it won't be included in production artifacts.
 */
public final class TestHooks {
    private TestHooks() {}

    public interface ExceptionListener<M> extends ExceptionNotifierRegistry.ExceptionListener<M> {}

    public static <M> void registerExceptionListener(ExceptionListener<M> l) {
        ExceptionNotifierRegistry.registerExceptionListener(l);
    }

    public static <M> void unregisterExceptionListener(ExceptionListener<M> l) {
        ExceptionNotifierRegistry.unregisterExceptionListener(l);
    }

    public static <M> void notifyException(Object publicRef, M message, Throwable t) {
        ExceptionNotifierRegistry.notifyException(publicRef, message, t);
    }
}

