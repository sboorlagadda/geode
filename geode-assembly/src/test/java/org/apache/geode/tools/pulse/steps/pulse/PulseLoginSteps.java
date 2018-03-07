package org.apache.geode.tools.pulse.steps.pulse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import cucumber.api.java8.En;
import org.junit.ClassRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.geode.test.junit.rules.LocatorStarterRule;

public class PulseLoginSteps implements En {
  public WebDriver driver;
  public LocatorStarterRule locator = new LocatorStarterRule().withJMXManager();

  public PulseLoginSteps() {

    Before(() -> {
      locator.startLocator();

      ChromeOptions options = new ChromeOptions();
      // options.addArguments("headless");
      options.addArguments("window-size=1200x600");
      driver = new ChromeDriver(options);
    });

    After(() -> {
      driver.quit();
      locator.after();
    });

    Given("^I have pulse started$", () -> {
      // pulse is already started with locator auto start
    });


    Given("I open pulse", () -> {
      driver.get("http://localhost:" + locator.getHttpPort() + "/pulse/");
      waitForElementById("user_name", 1);
    });

    Then("I see login page", () -> {
      WebElement userNameElement = driver.findElement(By.id("user_name"));
      WebElement passwordElement = driver.findElement(By.id("user_password"));

      assertThat(userNameElement).isNotNull();
      assertThat(passwordElement).isNotNull();
    });

    When("^I login using user \"([^\"]*)\" and password \"([^\"]*)\"$",
        (String username, String password) -> {
          WebElement userNameElement = driver.findElement(By.id("user_name"));
          WebElement passwordElement = driver.findElement(By.id("user_password"));
          userNameElement.sendKeys(username);
          passwordElement.sendKeys(password);
          passwordElement.submit();
        });

    Then("I see cluster details page", () -> {
      driver.get("http://localhost:" + locator.getHttpPort() + "/pulse/clusterDetail.html");
      WebElement userNameOnPulsePage =
          (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver d) {
              return d.findElement(By.id("userName"));
            }
          });
      assertNotNull(userNameOnPulsePage);
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
