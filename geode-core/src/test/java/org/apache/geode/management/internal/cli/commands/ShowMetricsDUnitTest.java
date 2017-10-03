/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.management.internal.cli.commands;


import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.CacheServerMXBean;
import org.apache.geode.management.ManagementService;
import org.apache.geode.management.MemberMXBean;
import org.apache.geode.management.RegionMXBean;
import org.apache.geode.test.dunit.rules.LocatorServerStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.DistributedTest;
import org.apache.geode.test.junit.rules.GfshShellConnectionRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import javax.management.ObjectName;

@Category(DistributedTest.class)
public class ShowMetricsDUnitTest {

  private MemberVM locator, server;

  @Rule
  public LocatorServerStartupRule lsRule = new LocatorServerStartupRule();

  @Rule
  public GfshShellConnectionRule gfsh = new GfshShellConnectionRule();

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void before() throws Exception {
    locator = lsRule.startLocatorVM(0);
    server = lsRule.startServerVM(1, locator.getPort());
    int serverPort = server.getPort();
    server.invoke(() -> {
      Cache cache = LocatorServerStartupRule.serverStarter.getCache();
      RegionFactory<Integer, Integer> dataRegionFactory =
          cache.createRegionFactory(RegionShortcut.REPLICATE);
      dataRegionFactory.create("REGION1");

      DistributedMember member = cache.getDistributedSystem().getDistributedMember();
      await().atMost(120, SECONDS).until(() -> isBeanReady(cache, 5, "", member, serverPort));
    });

    locator.invoke(() -> {
      Cache cache = LocatorServerStartupRule.locatorStarter.getLocator().getCache();
      // Wait for all of the relevant beans to be ready
      await().atMost(120, SECONDS).until(() -> isBeanReady(cache, 1, "", null, 0));
      await().atMost(120, SECONDS).until(() -> isBeanReady(cache, 2, "REGION1", null, 0));

      DistributedMember member = cache.getDistributedSystem().getDistributedMember();
      await().atMost(120, SECONDS).until(() -> isBeanReady(cache, 3, "", member, 0));
    });

    gfsh.connect(locator);
  }

  private static boolean isBeanReady(Cache cache, int beanType, String regionName,
      DistributedMember distributedMember, int cacheServerPort) {
    ManagementService mgmtService = ManagementService.getManagementService(cache);
    Object bean = null;

    switch (beanType) {
      case 1:
        bean = mgmtService.getDistributedSystemMXBean();
        break;
      case 2:
        bean = mgmtService.getDistributedRegionMXBean("/" + regionName);
        break;
      case 3:
        ObjectName memberMBeanName = mgmtService.getMemberMBeanName(distributedMember);
        bean = mgmtService.getMBeanInstance(memberMBeanName, MemberMXBean.class);
        break;
      case 4:
        ObjectName regionMBeanName =
            mgmtService.getRegionMBeanName(distributedMember, "/" + regionName);
        bean = mgmtService.getMBeanInstance(regionMBeanName, RegionMXBean.class);
        break;
      case 5:
        ObjectName csMxBeanName =
            mgmtService.getCacheServerMBeanName(cacheServerPort, distributedMember);
        bean = mgmtService.getMBeanInstance(csMxBeanName, CacheServerMXBean.class);
        break;
    }

    return bean != null;
  }

  @Test
  public void testShowMetricsDefault() throws Exception {
    gfsh.executeAndVerifyCommand("show metrics");
  }

  @Test
  public void testShowMetricsRegion() throws Exception {
    gfsh.executeAndVerifyCommand("show metrics --region=REGION1");
    assertThat(gfsh.getGfshOutput()).contains("Cluster-wide Region Metrics");
  }

  @Test
  public void testShowMetricsMember() throws Exception {
    gfsh.executeAndVerifyCommand(
        "show metrics --member=" + server.getName() + " --port=" + server.getPort());
    assertThat(gfsh.getGfshOutput()).contains("Member Metrics");
    assertThat(gfsh.getGfshOutput()).contains("cache-server");
  }

  @Test
  public void testShowMetricsMemberWithFileOutput() throws Exception {
    File output = tempFolder.newFile("memberMetricReport.csv");
    output.delete();

    gfsh.executeAndVerifyCommand("show metrics --member=" + server.getName() + " --port="
        + server.getPort() + " --file=" + output.getAbsolutePath());
    assertThat(gfsh.getGfshOutput()).contains("Member Metrics");
    assertThat(gfsh.getGfshOutput()).contains("cache-server");
    assertThat(gfsh.getGfshOutput())
        .contains("Member metrics exported to " + output.getAbsolutePath());
    assertThat(output).exists();
  }

  @Test
  public void testShowMetricsRegionFromMember() throws Exception {
    gfsh.executeAndVerifyCommand("show metrics --member=" + server.getName() + " --region=REGION1");
    assertThat(gfsh.getGfshOutput()).contains("Metrics for region:/REGION1 On Member server-1");
  }
}
