package com.utilities;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {

    private static final String LOG_PATTERN = "%1$tF %1$tT %4$-7s [%2$s] %5$s %n";
    private static final Level DEFAULT_LEVEL = Level.INFO;
    private static boolean initialized = false;

    private AppLogger() {
        // Prevent instantiation
    }

    /**
     * Returns a configured Logger for the given class.
     * On first invocation, it sets up the global handlers.
     *
     * @param clazz the class requesting a logger
     * @return a java.util.logging.Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            configureGlobalLogger();
            initialized = true;
        }
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setLevel(DEFAULT_LEVEL);
        return logger;
    }

    private static void configureGlobalLogger() {
        Logger root = Logger.getLogger("");
        // Remove default handlers
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        // Console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(DEFAULT_LEVEL);
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return String.format(
                        LOG_PATTERN,
                        record.getMillis(),
                        record.getLoggerName(),
                        record.getSourceMethodName(),
                        record.getLevel().getName(),
                        record.getMessage()
                );
            }
        });
        root.addHandler(consoleHandler);

        // Optional: File handler (uncomment to enable)

        try {
            FileHandler fileHandler = new FileHandler("src/main/java/com/utilities/app.log", true);
            fileHandler.setLevel(DEFAULT_LEVEL);
            fileHandler.setFormatter(consoleHandler.getFormatter());
            root.addHandler(fileHandler);
        } catch (IOException e) {
            root.log(Level.WARNING, "Failed to initialize file handler for logger", e);
        }

        root.setLevel(DEFAULT_LEVEL);
    }
}
