Feature: Create Region
  Scenario: Create region should fail when no member found
    Given I have gfsh
    And I connect to a locator
    When I execute "create region --name=FOO --type=REPLICATE"
    Then I see message "No Members Found"

  Scenario: Create region should fail when no name is specified
    Given I have gfsh
    And I connect to a locator
    When I execute "create region"
    Then I see message contains "You should specify option"

  Scenario: Create region should create region
    Given I have gfsh
    And I connect to a locator
    And I start a server with name "server1"
    When I execute "create region --name=FOO --type=REPLICATE"
    Then I see message contains "Region "/FOO" created on "server1"
