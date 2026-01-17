package base;

import configuration.LogDirectorySetup;
import configuration.ReadConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import utilities.LoggerUtil;
import utilities.ScreenshotUtil;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class TestBase {

    protected static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    protected static final ThreadLocal<WebDriverWait> waitThread = new ThreadLocal<>();
    public static final Logger logger = LoggerUtil.getLogger();
    public static ScreenshotUtil screenshotUtil;

    protected static final ReadConfig config = ReadConfig.getInstance();

    /**
     * Build ChromeOptions with anti-popup settings (password manager, leak detection UI, etc.)
     */
    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // Headless (for recent Chrome)
        if (headless) {
            options.addArguments("--headless=new");
        }

        // Stability for Docker/Linux
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        // To user a new browsing context to avoid persisted password manager state
        options.addArguments("--incognito");

        // Attempt to suppress password manager / breach/leak detection UI
        options.addArguments("--disable-features=PasswordLeakDetection,AutofillServerCommunication");
        options.addArguments("--disable-save-password-bubble");

        // Disable Chrome Password Manager + autofill
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);


        options.setExperimentalOption("prefs", prefs);

        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            // Edge supports headless; use new if available
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        return options;
    }

    /**
     * Initialize WebDriver for the current thread (Hooks calls this)
     */
    public static WebDriver initializeDriver() {
        if (driverThread.get() != null) {
            logger.info("WebDriver already initialized for this thread.");
            return driverThread.get();
        }

        // Ensure log directory exists
        LogDirectorySetup.createLogDirectory();

        String browser = System.getenv().getOrDefault("BROWSER", config.getBrowser()).toLowerCase();
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", String.valueOf(config.isHeadless()))
        );

        String windowSize = config.getWindowSize();
        boolean useRemote = Boolean.parseBoolean(System.getenv().getOrDefault("USE_REMOTE_DRIVER", "false"));

        logger.info("Initializing WebDriver for browser: {}", browser);
        logger.info("Using Remote WebDriver: {}", useRemote);

        WebDriver driver;

        try {
            if (useRemote) {
                String hubHost = System.getenv().getOrDefault("HUB_HOST", "selenium-hub");
                URL remoteUrl = new URL("http://" + hubHost + ":4444/wd/hub");

                // Keep capabilities for logging/compat, but use browser Options where possible
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability("browserName", browser);
                capabilities.setCapability("platformName", "LINUX");
                if (headless) {
                    capabilities.setCapability("headless", true);
                }

                switch (browser) {
                    case "chrome": {
                        ChromeOptions chromeOptions = buildChromeOptions(headless);
                        driver = new RemoteWebDriver(remoteUrl, chromeOptions);
                        break;
                    }
                    case "firefox": {
                        FirefoxOptions firefoxOptions = buildFirefoxOptions(headless);
                        driver = new RemoteWebDriver(remoteUrl, firefoxOptions);
                        break;
                    }
                    case "edge": {
                        EdgeOptions edgeOptions = buildEdgeOptions(headless);
                        driver = new RemoteWebDriver(remoteUrl, edgeOptions);
                        break;
                    }
                    case "safari": {
                        // Safari is macOS-only; requires a Safari node on a macOS machine.
                        // Also, Safari does NOT support headless.
                        if (headless) {
                            logger.warn("Safari does not support headless mode. Ignoring headless=true.");
                        }
                        SafariOptions safariOptions = new SafariOptions();
                        driver = new RemoteWebDriver(remoteUrl, safariOptions);
                        break;
                    }
                    default:
                        throw new RuntimeException("Unsupported browser for RemoteWebDriver: " + browser);
                }

            } else {
                // Local WebDriver
                switch (browser) {
                    case "chrome": {
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver(buildChromeOptions(headless));
                        break;
                    }
                    case "firefox": {
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver(buildFirefoxOptions(headless));
                        break;
                    }
                    case "edge": {
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver(buildEdgeOptions(headless));
                        break;
                    }
                    case "safari": {
                        // Safari only works on macOS and requires:
                        // Safari -> Develop -> Allow Remote Automation enabled
                        // Terminal (once): safari driver --enable
                        if (headless) {
                            logger.warn("Safari does not support headless mode. Ignoring headless=true.");
                        }
                        driver = new SafariDriver(new SafariOptions());
                        break;
                    }
                    default:
                        throw new RuntimeException("Unsupported browser: " + browser);
                }
            }

            // Window sizing
            if (windowSize.equalsIgnoreCase("maximize")) {
                driver.manage().window().maximize();
            } else if (windowSize.matches("\\d+x\\d+")) {
                String[] dims = windowSize.split("x");
                driver.manage().window().setSize(new Dimension(
                        Integer.parseInt(dims[0]),
                        Integer.parseInt(dims[1])
                ));
            }

            // Timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));

            // Store in ThreadLocal
            driverThread.set(driver);
            waitThread.set(wait);

            // Screenshot util (per thread)
            screenshotUtil = new ScreenshotUtil(driver, logger);

            logger.info("WebDriver initialized successfully for thread.");
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return driver;
    }

    /**
     * Get WebDriver for current thread
     */
    public static WebDriver getDriver() {
        if (driverThread.get() == null) {
            logger.info("Driver not initialized yet, initializing now...");
            return initializeDriver();
        }
        return driverThread.get();
    }

    /**
     * Get WebDriverWait for current thread
     */
    public static WebDriverWait getWait() {
        if (waitThread.get() == null) {
            logger.info("Wait not initialized yet, initializing driver first...");
            initializeDriver();
        }
        return waitThread.get();
    }

    /**
     * Quit WebDriver for current thread
     */
    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driverThread.remove();
                waitThread.remove();
                logger.info("WebDriver quit successfully for thread.");
            }
        }
    }

    /**
     * Capture screenshot using ScreenshotUtil
     */
    public static void captureScreen(String testName) throws IOException {
        if (screenshotUtil != null) {
            screenshotUtil.captureScreenshot(testName);
        } else {
            logger.warn("ScreenshotUtil not initialized for thread.");
        }
    }
}
