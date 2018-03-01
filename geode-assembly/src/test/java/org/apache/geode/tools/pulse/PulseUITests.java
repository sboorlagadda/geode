package org.apache.geode.tools.pulse;

import cucumber.api.java8.En;
import org.apache.geode.test.junit.categories.IntegrationTest;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@Category(IntegrationTest.class)
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    plugin = {"pretty", "html:target/cucumber-html-report"},
    tags = {}
)
public class PulseUITests implements En {
}
