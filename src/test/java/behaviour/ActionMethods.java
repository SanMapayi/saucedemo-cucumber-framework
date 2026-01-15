package behaviour;

import base.TestBase;
import io.cucumber.java.Scenario;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;

public class ActionMethods extends TestBase {

    public static final ActionMethods getInstance = new ActionMethods();

    private static Scenario scenario;

    private ActionMethods() {}

    /* ------------------ SCENARIO INJECTION ------------------ */

    public static void setScenario(Scenario sc) {
        scenario = sc;
    }

    /* ------------------ ENTER TEXT ------------------ */

    public void enterText(By locator, String value) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            element.clear();
            element.sendKeys(value);

            log("Entered text '" + value + "' into " + locator);

        } catch (Exception e) {
            handleError("Failed to enter text into " + locator, e);
        }
    }

    /* ------------------ CLICK ------------------ */

    public void click(By locator) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.elementToBeClickable(locator));

            element.click();
            log("Clicked on element " + locator);

        } catch (Exception e) {
            handleError("Click failed on " + locator, e);
        }
    }

    /* ------------------ IS DISPLAYED ------------------ */

    public boolean isDisplayed(By locator) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            log("Element is displayed: " + locator);
            return element.isDisplayed();

        } catch (TimeoutException e) {
            log("Element NOT displayed: " + locator);
            return false;
        }
    }

    /* ------------------ SELECT DROPDOWN ------------------ */

    public void selectByValue(By locator, String value) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            new Select(element).selectByValue(value);
            log("Selected value '" + value + "' from dropdown " + locator);

        } catch (Exception e) {
            handleError("Failed to select value '" + value + "' from " + locator, e);
        }
    }

    /* ------------------ VISIBILITY CONDITION ------------------ */

    public ExpectedCondition<Boolean> visibilityOfElement(By locator) {
        return driver -> {
            try {
                return getDriver().findElement(locator).isDisplayed();
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return false;
            }
        };
    }

    /* ------------------ GENERIC WAIT ------------------ */

    public static ExpectedCondition<Boolean> waitFor(boolean condition) {
        return driver -> condition;
    }

    /* ------------------ LOGGING HELPERS ------------------ */

    private void log(String message) {
        logger.info(message);
        if (scenario != null) {
            scenario.log(message);
        }
    }

    private void handleError(String message, Exception e) {
        String fullMessage = message + " | Error: " + e.getMessage();

        logger.error(fullMessage);
        if (scenario != null) {
            scenario.log(fullMessage);
        }

        try {
            captureScreen(scenario != null ? scenario.getName() : "Unknown");
        } catch (IOException ioException) {
            logger.error("Screenshot capture failed: {}", ioException.getMessage());
        }

        throw new RuntimeException(fullMessage, e);
    }
}
