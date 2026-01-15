package utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class ScreenshotUtil {

    private final WebDriver driver;
    private static Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);



    public ScreenshotUtil(WebDriver driver, Logger logger) {
        this.driver = driver;
        ScreenshotUtil.logger = logger;
    }

    /**
     * Captures a screenshot only if a file with the given screenshotName does not already exist.
     * This is useful when the screenshotName is derived from the test name, and in data-driven
     * tests the test name remains the same even if the data differs.
     *
     * @param screenshotName a consistent identifier for the test failure
     */

    public void captureScreenshot(String screenshotName) {
        // Create a timestamp for unique screenshot names
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = screenshotName + "_" + timeStamp + ".png";
        String filePath = System.getProperty("user.dir") + "/screenshots/" + fileName;

        try {
            // Cast driver to TakesScreenshot and capture the screenshot
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destinationFile);
            logger.info("Screenshot saved at: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }

    public static void deleteFailedScreenshotsInFolder(Path screenshotPath) throws IOException {
        if (Files.exists(screenshotPath) && Files.isDirectory(screenshotPath)) {
            try (Stream<Path> files = Files.walk(screenshotPath)) {
                files.filter(Files::isRegularFile) // Select only files, not directories
                        .forEach(file -> {
                            try {
                                Files.delete(file);
                                logger.info("Deleted: {}", file);
                            } catch (IOException e) {
                                logger.error("Failed to delete: {} - {}", file, e.getMessage());
                            }
                        });
            }
        }
    }
}