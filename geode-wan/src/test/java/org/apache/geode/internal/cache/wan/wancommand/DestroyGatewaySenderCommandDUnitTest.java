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
 */
package org.apache.geode.internal.cache.wan.wancommand;

import static org.apache.geode.distributed.ConfigurationProperties.DISTRIBUTED_SYSTEM_ID;
import static org.apache.geode.distributed.ConfigurationProperties.NAME;
import static org.apache.geode.distributed.ConfigurationProperties.REMOTE_LOCATORS;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.DistributedTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;

@Category(DistributedTest.class)
public class DestroyGatewaySenderCommandDUnitTest {

  public static final String CREATE =
      "create gateway-sender --id=sender --remote-distributed-system-id=2";
  public static final String DESTROY = "destroy gateway-sender --id=sender";

  @ClassRule
  public static ClusterStartupRule clusterStartupRule = new ClusterStartupRule();

  @Rule
  public GfshCommandRule gfsh = new GfshCommandRule();

  private static MemberVM locatorSite1;
  private static MemberVM server1;

  @BeforeClass
  public static void beforeClass() throws Exception {
    Properties props = new Properties();
    props.setProperty(NAME, "happylocator");
    props.setProperty(DISTRIBUTED_SYSTEM_ID, "" + 1);
    locatorSite1 = clusterStartupRule.startLocatorVM(0, props);

    props.setProperty(DISTRIBUTED_SYSTEM_ID, "" + 2);
    props.setProperty(REMOTE_LOCATORS, "localhost[" + locatorSite1.getPort() + "]");
    clusterStartupRule.startLocatorVM(2, props);

    props.setProperty(NAME, "happyserver1");
    server1 = clusterStartupRule.startServerVM(3, props, locatorSite1.getPort());
  }

  @Before
  public void before() throws Exception {
    gfsh.connectAndVerify(locatorSite1);
  }

  @After
  public void after() {
    // gfsh.executeAndAssertThat(DESTROY + " --if-exists").statusIsSuccess();
  }

  /**
   * GatewaySender with all default attributes
   */
  @Test
  public void testCreateDestroyGatewaySenderWithDefault() throws Exception {
    gfsh.executeAndAssertThat(CREATE).statusIsSuccess().tableHasColumnWithExactValuesInAnyOrder(
        "Status", "GatewaySender \"sender\" created on \"happyserver1\"");

    gfsh.execute("list gateways");

    // destroy gateway sender and verify AEQs cleaned up
    gfsh.executeAndAssertThat(DESTROY).statusIsSuccess().tableHasColumnWithExactValuesInAnyOrder(
        "Status", "GatewaySender \"sender\" destroyed on \"happyserver1\"");

    gfsh.execute("list gateways");
  }
}
