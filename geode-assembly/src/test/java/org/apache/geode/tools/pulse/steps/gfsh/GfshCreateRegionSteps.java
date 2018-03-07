package org.apache.geode.tools.pulse.steps.gfsh;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java8.En;

import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;


public class GfshCreateRegionSteps implements En {
  public LocatorStarterRule locator = new LocatorStarterRule();
  public GfshCommandRule gfsh;
  public String commandResult;

  public GfshCreateRegionSteps() {

    Before(() -> locator.startLocator());
    After(() -> locator.after());

    Given("^I have gfsh$", () -> gfsh = new GfshCommandRule());

    Given("^I connect to a locator$", () -> gfsh.connectAndVerify(locator));

    When("^I execute \"([^\"]*)\"$", (String command) -> commandResult = gfsh.execute(command));

    Then("^I see message \"([^\"]*)\"$",
        (String message) -> assertThat(commandResult.trim()).isEqualTo(message));
  }
}
