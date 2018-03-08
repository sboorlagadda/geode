package org.apache.geode.tools.pulse.steps.gfsh;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java8.En;

import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.apache.geode.tools.pulse.GfshCreateRegionTests;

import java.util.Properties;


public class GfshCreateRegionSteps implements En {
  public GfshCommandRule gfsh;
  public String commandResult;

  public GfshCreateRegionSteps() {

    //Before(() -> GfshCreateRegionTests.locator.startLocator());
    After(() -> {
      gfsh.disconnect();
      GfshCreateRegionTests.cluster.stopVM(1);
    });

    Given("^I have gfsh$", () -> gfsh = new GfshCommandRule());

    Given("^I connect to a locator$", () -> gfsh.connectAndVerify(GfshCreateRegionTests.locator));

    When("^I execute \"([^\"]*)\"$", (String command) -> {
      System.out.println("Command....received" + command);
      commandResult = gfsh.execute(command);
    });

    When("^I start a server with name \"([^\"]*)\"$", (String name) -> {
      Properties properties = new Properties();
      properties.setProperty("name", name);
      GfshCreateRegionTests.cluster.startServerVM(1, properties,GfshCreateRegionTests.locator.getPort());
    });

    Then("^I see message \"([^\"]*)\"$",
        (String message) -> assertThat(commandResult.trim()).isEqualTo(message));

    Then("^I see message contains \"(.*)\"$",
        (String message) -> assertThat(commandResult.trim()).contains(message));
  }
}
