/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.apache.geode.tools.pulse.ui;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebDriver;

import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.junit.categories.UITest;
import org.apache.geode.test.junit.rules.EmbeddedPulseRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.apache.geode.test.junit.rules.ScreenshotOnFailureRule;
import org.apache.geode.test.junit.rules.WebDriverRule;
import org.apache.geode.tools.pulse.internal.data.Cluster;

@Category(UITest.class)
public class PulseNoAuthTest extends PulseBase {

  @ClassRule
  public static LocatorStarterRule locator =
      new LocatorStarterRule().withJMXManager().withAutoStart();

  @ClassRule
  public static ClusterStartupRule clusterRule = new ClusterStartupRule();

  @Rule
  public EmbeddedPulseRule pulseRule = new EmbeddedPulseRule();

  @Rule
  public WebDriverRule webDriverRule = new WebDriverRule("admin", "admin", getPulseURL());

  @Rule
  public ScreenshotOnFailureRule screenshotOnFailureRule =
      new ScreenshotOnFailureRule(this::getWebDriver);

  private Cluster cluster;

  @BeforeClass
  public static void beforeClass() {
    int locatorPort = locator.getPort();
    clusterRule.startServerVM(1, x -> x.withConnectionToLocator(locatorPort));
  }

  @Before
  public void before() {
    pulseRule.useJmxManager("localhost", locator.getJmxPort());
    cluster = pulseRule.getRepository().getCluster("admin", null);
  }

  public WebDriver getWebDriver() {
    return webDriverRule.getDriver();
  }

  @Override
  public String getPulseURL() {
    return "http://localhost:" + locator.getHttpPort() + "/pulse/";
  }

  @Override
  public Cluster getCluster() {
    return this.cluster;
  }
}
