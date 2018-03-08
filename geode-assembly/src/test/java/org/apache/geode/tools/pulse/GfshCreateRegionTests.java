package org.apache.geode.tools.pulse;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.apache.geode.test.junit.categories.IntegrationTest;

@Category(IntegrationTest.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/gfsh",
    glue = {"org/apache/geode/tools/pulse/steps/gfsh"},
    plugin = {"pretty", "html:target/cucumber-html-report"}, tags = {})
public class GfshCreateRegionTests {
  @ClassRule
  public static LocatorStarterRule locator = new LocatorStarterRule().withAutoStart();
  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();
}
