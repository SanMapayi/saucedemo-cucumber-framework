package pages;

import base.TestBase;
import behaviour.ActionMethods;
import behaviour.GetMethods;
import core.Constants;
import org.openqa.selenium.By;

/**
 * Page Object representing the SauceDemo Login page.
 * This class contains:
 *  - Locators
 *  - Actions (navigate, type, click)
 *  - Data getters (read text/visibility)
 */
public class LoginPage extends TestBase {

    /** Helper for interactions like click/type/isDisplayed (singleton).
     * Implemented as a singleton to ensure consistency.
     * */
    private static final ActionMethods actionMethods = ActionMethods.getInstance;

    /**
     * Centralised getter helper used for retrieving text and values
     * from UI elements with built-in waiting logic.
     */
    private static final GetMethods getMethods = GetMethods.getInstance;

    /**
     * Expected text displayed on the login page logo.
     * Used to confirm that the user is on the correct page.
     */
    public static final String EXPECTED_LOGIN_LOGO_TEXT = "Swag Labs";

    // ----------------------- Locators -----------------------

    /** Username input field. */
    private final By usernameLocator = By.id("user-name");

    /** Password input field. */
    private final By passwordLocator = By.id("password");

    /** Login page logo (useful to confirm page identity). */
    private final By loginLogoLocator = By.className("login_logo");

    /** Login button that submits the login form. */
    private final By loginButtonLocator = By.id("login-button");

    // ----------------------- Actions -----------------------

    /**
     * Navigates the browser to the application's base URL.
     * URL is stored centrally (Constants) to avoid hardcoding.
     */
    public void navigateToBaseUrl() {
        TestBase.getDriver().get(Constants.URL);
    }

    /**
     * Types the supplied credentials and clicks the login button.
     */
    public void login(String username, String password) {
        actionMethods.enterText(usernameLocator, username);
        actionMethods.enterText(passwordLocator, password);
        actionMethods.click(loginButtonLocator);
    }

    // ----------------------- Getters / Page State -----------------------

    /** Returns the visible text of the login logo (e.g., "Swag Labs"). */
    public String getLoginLogoText() {
        return getMethods.getText(loginLogoLocator).trim();
    }

    /** Returns true if the login logo is displayed (page identity check). */
    public boolean isLoginLogoDisplayed() {
        return actionMethods.isDisplayed(loginLogoLocator);
    }
}
