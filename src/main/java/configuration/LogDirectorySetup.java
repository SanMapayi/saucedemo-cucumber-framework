package configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogDirectorySetup {
    public static void createLogDirectory() {
        String logDirectory = "logfiles";  // Path to log directory
        Path path = Paths.get(logDirectory);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);  // Create the directory structure
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create log directory: " + logDirectory);
            }
        }
    }
}
