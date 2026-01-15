package utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    // Singleton Logger instance
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    // Get the logger instance
    public static Logger getLogger() {
        return logger;
    }
}
