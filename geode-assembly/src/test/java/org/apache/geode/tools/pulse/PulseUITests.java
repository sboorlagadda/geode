package org.apache.geode.tools.pulse;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.apache.geode.test.junit.categories.IntegrationTest;

@Category(IntegrationTest.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
    plugin = {"pretty", "html:target/cucumber-html-report"}, tags = {})
public class PulseUITests {
}
