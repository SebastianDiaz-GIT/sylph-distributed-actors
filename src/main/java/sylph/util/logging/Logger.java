package sylph.util.logging;

import java.time.Instant;

/**
 * Logger muy simple para el framework. Esto es intencionalmente mínimo para evitar
 * dependencias externas. En el futuro se puede integrar SLF4J/Log4J.
 */
public class Logger {
    private final String name;

    private Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(Class<?> cls) {
        return new Logger(cls.getSimpleName());
    }

    public void info(String msg) {
        System.out.println(Instant.now() + " [INFO] " + name + " - " + msg);
    }

    public void warn(String msg) {
        System.out.println(Instant.now() + " [WARN] " + name + " - " + msg);
    }

    public void error(String msg, Throwable t) {
        System.out.println(Instant.now() + " [ERROR] " + name + " - " + msg);
        if (t != null) t.printStackTrace(System.err);
    }
}

