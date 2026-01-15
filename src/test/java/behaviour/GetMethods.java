package behaviour;

import base.TestBase;
import io.cucumber.java.Scenario;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GetMethods extends TestBase {

    public static final GetMethods getInstance = new GetMethods();

    private static Scenario scenario;

    private GetMethods() {}

    /* ------------------ SCENARIO INJECTION ------------------ */

    public static void setScenario(Scenario sc) {
        scenario = sc;
    }

    /* ------------------ GET TEXT ------------------ */

    public String getText(By locator) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            String text = element.getText();
            if (text == null || text.trim().isEmpty()) {
                text = element.getAttribute("innerText");
            }

            log("Retrieved text '" + text + "' from " + locator);
            return text;

        } catch (Exception e) {
            handleError("Failed to get text from " + locator, e);
            return "";
        }
    }

    /* ------------------ GET VALUE USING JAVASCRIPT ------------------ */

    public String getTextByJS(String label, By locator) {
        try {
            WebElement element = getWait()
                    .until(ExpectedConditions.presenceOfElementLocated(locator));

            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            String value = (String) js.executeScript(
                    "return arguments[0].textContent;", element);

            if (value != null && !value.trim().isEmpty()) {
                log("Retrieved value for '" + label + "': " + value.trim());
                return value.trim();
            } else {
                log("No value found for '" + label + "'");
                return "";
            }

        } catch (Exception e) {
            handleError("Failed to retrieve value for '" + label + "' from " + locator, e);
            return "";
        }
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
    }
}
