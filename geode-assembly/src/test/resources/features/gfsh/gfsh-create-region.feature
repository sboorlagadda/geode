Feature: Create Region
  Scenario: Create region should fail when no member found
    Given I have gfsh
    And I connect to a locator
    When I execute "create region --name=FOO --type=REPLICATE"
    Then I see message "No Members Found"
