Feature: Pulse Authentication
  Scenario: Should navigate to clusterDetail page
    When I open pulse
    And I see login page
    And I login using user "admin" and password "admin"
    Then I see cluster details page
