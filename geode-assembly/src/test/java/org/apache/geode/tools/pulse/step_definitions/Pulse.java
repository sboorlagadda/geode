package org.apache.geode.tools.pulse.step_definitions;

import cucumber.api.java8.En;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.junit.ClassRule;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class Pulse implements En {
  public WebDriver driver;

  @ClassRule
  public static LocatorStarterRule locator = new LocatorStarterRule().withJMXManager();

  public Pulse() {
    Before(() -> locator.startLocator());

    ChromeOptions options = new ChromeOptions();
    options.addArguments("headless");
    options.addArguments("window-size=1200x600");
    driver = new ChromeDriver(options);

    Given("I open pulse", () -> {
      driver.get("http://localhost:"+ locator.getHttpPort()+"/pulse/");
      waitForElementById("user_name", 1);
    });

    Then("I see login page", () -> {
      WebElement userNameElement = driver.findElement(By.id("user_name"));
      WebElement passwordElement = driver.findElement(By.id("user_password"));

      assertThat(userNameElement).isNotNull();
      assertThat(passwordElement).isNotNull();
    });
  }

  public WebElement waitForElementById(final String id, int timeoutInSeconds) {
    WebElement element =
        (new WebDriverWait(driver, timeoutInSeconds)).until(new ExpectedCondition<WebElement>() {
          @Override
          public WebElement apply(WebDriver d) {
            return d.findElement(By.id(id));
          }
        });
    assertNotNull(element);
    return element;
  }
}
