package hooks;

import base.TestBase;
import behaviour.ActionMethods;
import behaviour.GetMethods;
import configuration.LogDirectorySetup;
import core.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import utilities.FileUtils;
import utilities.ScreenshotUtil;

import java.io.IOException;

public class Hooks {

    private static final Logger logger = TestBase.logger;
    private static boolean isLogDirCreated = false;

    @BeforeAll
    public static void globalSetup() throws IOException {
        logger.info("BEFORE ALL SCENARIO HOOK CALLED:");
        if (!isLogDirCreated) {
            LogDirectorySetup.createLogDirectory();
            isLogDirCreated = true;
            logger.info("Log directory setup complete.");
        }
        ScreenshotUtil.deleteFailedScreenshotsInFolder(Constants.SCREENSHOTSPATH);
        FileUtils.deleteTestReports(Constants.NUMBEROFTESTREPORTSTOKEEP);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("BEFORE SCENARIO HOOK CALLED:");
        ActionMethods.setScenario(scenario);
        GetMethods.setScenario(scenario);
        logger.info("Starting scenario: {}", scenario.getName());
        // Initialize WebDriver using TestBase
        TestBase.initializeDriver();

        // Re-initialize ScreenshotUtil with current driver
        TestBase.screenshotUtil = new ScreenshotUtil(TestBase.getDriver(), logger);
    }

    @After
    public void afterScenario(Scenario scenario) {
        logger.info("AFTER SCENARIO HOOK CALLED:");
        try {
            if (scenario.isFailed()) {
                logger.error("Scenario failed: {}", scenario.getName());

                // Capture screenshot for report
                byte[] screenshotBytes = ((org.openqa.selenium.TakesScreenshot) TestBase.getDriver())
                        .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                scenario.attach(screenshotBytes, "image/png", scenario.getName());

                // Save screenshot to file using ScreenshotUtil
                TestBase.screenshotUtil.captureScreenshot(scenario.getName());
            } else {
                logger.info("Scenario passed: {}", scenario.getName());
            }
        } catch (Exception e) {
            logger.error("Failed during afterScenario hook: {}", e.getMessage());
        } finally {
            // Quit WebDriver after each scenario
            TestBase.quitDriver();
        }
    }
}
