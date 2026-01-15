Feature: Add highest priced item to cart

  As a user of SauceDemo
  I want to add the highest priced item to my cart
  So that I can verify pricing logic without using sort

  @required @smoke
  Scenario: Standard user adds the highest priced item to the cart
    Given I navigate to the login page
    When I login with username "standard_user" and password "secret_sauce"
    And I select the highest priced item without using sort
    And I add the selected item to the cart
    Then the cart should contain the selected highest priced item

  @optional @regression
  Scenario Outline: Other users add the highest priced item to the cart
    Given I navigate to the login page
    When I login with username "<username>" and password "secret_sauce"
    And I select the highest priced item without using sort
    And I add the selected item to the cart
    Then the cart should contain the selected highest priced item

    Examples:
      | username                |
      | standard_user          |
      | performance_glitch_user |
      | error_user             |
      | visual_user            |
