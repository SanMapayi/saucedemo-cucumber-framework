package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import pages.ProductsPage;

public class SauceDemoSteps {

    private final LoginPage loginPage = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();

    //The chosen item so later steps can validate name/price match.
    private ProductsPage.ProductInfo highestPriceItem;


    @Given("I navigate to the login page")
    public void iNavigateToTheLoginPage() {
        loginPage.navigateToBaseUrl();

        // Assertions
        Assert.assertTrue(loginPage.isLoginLogoDisplayed(), "Login logo is not visible - login page may not be loaded.");
        Assert.assertEquals(
                loginPage.getLoginLogoText(),
                LoginPage.EXPECTED_LOGIN_LOGO_TEXT,
                "Unexpected login logo text - user may not be on the login page."
        );
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        loginPage.login(username, password);

        // Assertions
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Products page was not displayed after login.");
        Assert.assertEquals(productsPage.getProductsPageTitle(), "Products", "Unexpected Products page title.");
    }

    @When("I select the highest priced item without using sort")
    public void iSelectTheHighestPricedItemWithoutUsingSort() {
        highestPriceItem = productsPage.findHighestPricedItemFromList();

        // Business assertions about selection
        Assert.assertNotNull(highestPriceItem, "Highest item was null.");
        Assert.assertTrue(highestPriceItem.index() > 0, "Highest item index was invalid.");
        Assert.assertFalse(highestPriceItem.name().isEmpty(), "Highest item name was empty.");

        productsPage.openItemDetails(highestPriceItem);

        // Business assertion: details page should match selected item (name + price)
        Assert.assertEquals(productsPage.getDetailsItemName(), highestPriceItem.name(),
                "Details page item name does not match selected highest priced item.");
        Assert.assertEquals(productsPage.getDetailsItemPrice(), highestPriceItem.price(),
                "Details page item price does not match selected highest priced item.");
    }

    @When("I add the selected item to the cart")
    public void iAddTheSelectedItemToTheCart() {
        productsPage.addToCartFromDetails();

        // Business assertion: badge should show exactly 1 after adding
        Assert.assertEquals(productsPage.getCartBadgeCount(), 1, "Cart badge count is not 1 after adding item.");
    }

    @Then("the cart should contain the selected highest priced item")
    public void theCartShouldContainTheSelectedHighestPricedItem() {
        productsPage.openCart();

        // Business assertion: cart page opened correctly
        Assert.assertEquals(productsPage.getCartPageTitle(), "Your Cart", "Cart page title mismatch.");
        Assert.assertTrue(productsPage.isRemoveButtonVisible(), "Remove button not visible (cart may be empty).");

        // Business assertion: correct item is present in cart (name + price)
        Assert.assertEquals(productsPage.getCartItemName(), highestPriceItem.name(),
                "Cart item name does not match selected highest priced item.");
        Assert.assertEquals(productsPage.getCartItemPrice(), highestPriceItem.price(),
                "Cart item price does not match selected highest priced item.");
    }
}
