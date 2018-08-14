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
package org.apache.geode.cache.client.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.ssl.ClusterSSLProvider;
import org.apache.geode.test.dunit.SerializableConsumerIF;
import org.apache.geode.test.dunit.rules.ClientVM;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;

@Category({ClientServerTest.class})
public class CacheServerSSLDistributedTest {
  private static MemberVM locator;
  private static MemberVM server;
  private static ClientVM client;

  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();

  @ClassRule
  public static GfshCommandRule gfsh = new GfshCommandRule();

  @BeforeClass
  public static void setupCluster() throws Exception {
    ClusterSSLProvider sslProvider = new ClusterSSLProvider();
    sslProvider.withServerCertificate("server", InetAddress.getLocalHost())
        .withClientCertificate("client");

    Properties serverSSLProps = sslProvider.generateServerPropertiesWith();
    Properties clientSSLProps = sslProvider.generateClientPropertiesWith();

    // create a cluster
    locator = cluster.startLocatorVM(0, serverSSLProps);
    server = cluster.startServerVM(1, serverSSLProps, locator.getPort());

    // create region
    server.invoke(CacheServerSSLDistributedTest::createServerRegion);
    locator.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // setup client
    setupClient(clientSSLProps, server.getPort(), "192.168.254.45");
  }

  private static void createServerRegion() {
    RegionFactory factory =
        ClusterStartupRule.getCache().createRegionFactory(RegionShortcut.REPLICATE);
    Region r = factory.create("region");
    r.put("serverkey", "servervalue");
  }

  private static void setupClient(Properties clientSSLProps, int serverPort,
      String serverHost) throws Exception {
    SerializableConsumerIF<ClientCacheFactory> clientSetup = cf -> {
      cf.addPoolServer(serverHost, serverPort);
    };

    client = cluster.startClientVM(2, clientSSLProps, clientSetup);

    // create a client region
    client.invoke(CacheServerSSLDistributedTest::createClientRegion);
  }

  private static void createClientRegion() {
    ClientRegionFactory<String, String> regionFactory =
        ClusterStartupRule.getClientCache().createClientRegionFactory(ClientRegionShortcut.PROXY);
    Region<String, String> region = regionFactory.create("region");
    assertThat(region).isNotNull();
  }

  @Test
  public void testClientSSLConnection() {
    client.invoke(CacheServerSSLDistributedTest::doClientRegionTest);
    server.invoke(CacheServerSSLDistributedTest::doServerRegionTest);
  }

  private static void doClientRegionTest() {
    Region<String, String> region = ClusterStartupRule.getClientCache().getRegion("region");
    assertThat("servervalue").isEqualTo(region.get("serverkey"));

    region.put("clientkey", "clientvalue");
    assertThat("clientvalue").isEqualTo(region.get("clientkey"));
  }

  private static void doServerRegionTest() {
    Region<String, String> region = ClusterStartupRule.getCache().getRegion("region");
    assertThat("servervalue").isEqualTo(region.get("serverkey"));
    assertThat("clientvalue").isEqualTo(region.get("clientkey"));
  }
}
