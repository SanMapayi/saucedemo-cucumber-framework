package pages;

import base.TestBase;
import behaviour.ActionMethods;
import behaviour.GetMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for SauceDemo Products/Inventory + Product Details + Cart.
 * This class focuses on:
 *  - Locators
 *  - Interactions (click/type/navigation)
 *  - Returning data (names/prices/counts)
 */
public class ProductsPage {

    private static final ActionMethods actionMethods = ActionMethods.getInstance;
    private static final GetMethods getMethods = GetMethods.getInstance;

    // ----------------------- Inventory Page Locators -----------------------

    /** Title on the products page (usually "Products"). */
    private final By productPageTitleLocator = By.className("title");

    /** All inventory item cards on the products page. */
    private final By inventoryListLocator = By.cssSelector(".inventory_list > .inventory_item");

    // ----------------------- Product Details Page Locators -----------------------

    /** Product name on details page. */
    private final By itemDetailsNameLocator = By.cssSelector(".inventory_details_name");

    /** Product price on details page. */
    private final By itemDetailsPriceLocator = By.cssSelector(".inventory_details_price");

    /** "Add to cart" button on details page. */
    private final By addItemToCartLocator = By.cssSelector(".inventory_details_desc_container button#add-to-cart");

    // ----------------------- Cart Locators -----------------------

    /** Cart icon (top right). */
    private final By shoppingCartLocator = By.className("shopping_cart_link");

    /**
     * Cart badge element.
     * Note: this may be absent when there are 0 items in the cart.
     */
    private final By shoppingCartBadgeLocator = By.className("shopping_cart_badge");

    /** Cart page title (expected: "Your Cart"). */
    private final By shoppingCartPageTitleLocator = By.className("title");

    /** Item name and price inside cart. */
    private final By itemInCartNameLocator = By.className("inventory_item_name");
    private final By itemInCartPriceLocator = By.className("inventory_item_price");

    /** Remove button present when an item is in the cart. */
    private final By shoppingCartRemoveButtonLocator = By.xpath("//button[contains(text(), 'Remove')]");

    // ----------------------- Value Object -----------------------

    /**
     * Immutable container for the item we selected as "highest priced".
     * This avoids storing mutable state in the page object.
     */
    public record ProductInfo(int index, String name, double price) {}

    // ----------------------- Page Checks (no assertions) -----------------------

    /** Returns true if the Products page title is visible. */
    public boolean isProductsPageDisplayed() {
        return actionMethods.isDisplayed(productPageTitleLocator);
    }

    /** Returns the products page title text (e.g., "Products"). */
    public String getProductsPageTitle() {
        return getMethods.getText(productPageTitleLocator).trim();
    }

    // ----------------------- Highest price logic -----------------------

    /**
     * Finds the highest priced item from the inventory list WITHOUT using sort.
     * Returns ProductInfo containing index (1-based), name, and numeric price.
     */
    public ProductInfo findHighestPricedItemFromList() {
        List<WebElement> items = TestBase.getDriver().findElements(inventoryListLocator);

        // If this is 0, the test should fail — but the assertion will happen in Steps.
        int size = items.size();

        double highest = Double.NEGATIVE_INFINITY;
        int bestIndex = -1;

        for (int i = 1; i <= size; i++) {
            By priceLocator = By.cssSelector(
                    ".inventory_list > .inventory_item:nth-child(%d) div.pricebar .inventory_item_price".formatted(i)
            );

            double price = parseDollarPrice(getMethods.getText(priceLocator));

            if (price > highest) {
                highest = price;
                bestIndex = i;
            }
        }

        // Name for the highest price index
        By nameLocator = By.cssSelector(
                ".inventory_list > .inventory_item:nth-child(%d) .inventory_item_name[data-test='inventory-item-name']"
                        .formatted(bestIndex)
        );

        String name = getMethods.getText(nameLocator).trim();

        TestBase.logger.info("Highest priced item found: index={}, name='{}', price={}", bestIndex, name, highest);
        return new ProductInfo(bestIndex, name, highest);
    }

    /**
     * Clicks an item on the inventory page using its index.
     * This navigates to the single product details page.
     */
    public void openItemDetails(ProductInfo item) {
        By itemNameLocator = By.cssSelector(
                ".inventory_list > .inventory_item:nth-child(%d) .inventory_item_name[data-test='inventory-item-name']"
                        .formatted(item.index())
        );
        actionMethods.click(itemNameLocator);
    }

    // ----------------------- Details page getters/actions -----------------------

    public String getDetailsItemName() {
        return getMethods.getText(itemDetailsNameLocator).trim();
    }

    public double getDetailsItemPrice() {
        return parseDollarPrice(getMethods.getText(itemDetailsPriceLocator));
    }

    public void addToCartFromDetails() {
        actionMethods.click(addItemToCartLocator);
    }

    // ----------------------- Cart helpers -----------------------

    /** Returns cart badge count; returns 0 when badge is missing or empty. */
    public int getCartBadgeCount() {
        List<WebElement> badges = TestBase.getDriver().findElements(shoppingCartBadgeLocator);
        if (badges.isEmpty()) return 0;

        String text = badges.getFirst().getText().trim();
        if (text.isEmpty()) return 0;

        return Integer.parseInt(text);
    }

    /** Opens cart by clicking the cart icon. */
    public void openCart() {
        actionMethods.click(shoppingCartLocator);
    }

    public String getCartPageTitle() {
        return getMethods.getText(shoppingCartPageTitleLocator).trim();
    }

    public boolean isRemoveButtonVisible() {
        return actionMethods.isDisplayed(shoppingCartRemoveButtonLocator);
    }

    public String getCartItemName() {
        return getMethods.getText(itemInCartNameLocator).trim();
    }

    public double getCartItemPrice() {
        return parseDollarPrice(getMethods.getText(itemInCartPriceLocator));
    }

    // ----------------------- Utility -----------------------

    /**
     * This is a technical guard. It throws a clear error if the format is unexpected.
     */
    private double parseDollarPrice(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Price text was null");
        }

        String trimmed = text.trim();
        if (!trimmed.startsWith("$")) {
            throw new IllegalArgumentException("Unexpected price format: " + trimmed);
        }

        return Double.parseDouble(trimmed.substring(1));
    }
}
