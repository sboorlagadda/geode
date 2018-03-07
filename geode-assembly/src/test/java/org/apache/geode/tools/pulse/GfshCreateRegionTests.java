package org.apache.geode.tools.pulse;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.apache.geode.test.junit.categories.IntegrationTest;

@Category(IntegrationTest.class)
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/gfsh",
    glue = {"org/apache/geode/tools/pulse/steps/gfsh"},
    plugin = {"pretty", "html:target/cucumber-html-report"}, tags = {})
public class GfshCreateRegionTests {
}
