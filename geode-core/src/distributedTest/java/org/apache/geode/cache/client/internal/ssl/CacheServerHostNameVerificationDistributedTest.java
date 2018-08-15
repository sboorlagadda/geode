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
package org.apache.geode.cache.client.internal.ssl;

import static org.apache.geode.security.SecurableCommunicationChannels.ALL;
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
import org.apache.geode.cache.ssl.CertStores;
import org.apache.geode.cache.ssl.TestSSLUtils.CertificateBuilder;
import org.apache.geode.test.dunit.SerializableConsumerIF;
import org.apache.geode.test.dunit.rules.ClientVM;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.ClientServerTest;

@Category({ClientServerTest.class})
public class CacheServerHostNameVerificationDistributedTest {
  private static MemberVM locator;
  private static MemberVM server;
  private static ClientVM client;

  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();


  // Touch Points
  // Membership, Discovery, Request Processing
  // 1. Locator -> Locator
  // 2. Locator -> Server
  // 3. Server -> Locator - implicit covers #2
  // 4. Server -> Server
  // 5. Client -> Locator
  // 6. Client -> Server
  // 7. Gfsh -> locator (JMX)
  // JMX, REDIS, MEMCACHE
  // WAN - GWS -> GWR, L11 -> L21
  // Protobuf
  // Gfsh, Pulse, Admin REST, DEV REST - HTTPS

  // Mutual Auth
  // May be effects Clients(apps) -> Server
  // ??? Peer2Peer -> what happens?

  // Crosscheck about deploy jar socket

  @BeforeClass
  public static void setupCluster() throws Exception {
    CertificateBuilder locatorCertificate = new CertificateBuilder()
        .commonName("locator")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertificateBuilder serverCertificate = new CertificateBuilder()
        .commonName("server")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertificateBuilder clientCertificate = new CertificateBuilder()
        .commonName("client");

    CertStores locatorStore = CertStores.locatorStore();
    locatorStore.withCertificate(locatorCertificate);

    CertStores serverStore = CertStores.serverStore();
    serverStore.withCertificate(serverCertificate);

    CertStores clientStore = CertStores.clientStore();
    clientStore.withCertificate(clientCertificate);

    Properties locatorSSLProps = locatorStore
        .trustSelf()
        .trust(serverStore.alias(), serverStore.certificate())
        .trust(clientStore.alias(), clientStore.certificate())
        .propertiesWith(ALL, "any", "any");

    Properties serverSSLProps = serverStore
        .trustSelf()
        .trust(locatorStore.alias(), locatorStore.certificate())
        .trust(clientStore.alias(), clientStore.certificate())
        .propertiesWith(ALL, "any", "any");

    Properties clientSSLProps = clientStore
        .trust(locatorStore.alias(), locatorStore.certificate())
        .trust(serverStore.alias(), serverStore.certificate())
        .propertiesWith(ALL, "any", "any");

    // create a cluster
    locator = cluster.startLocatorVM(0, locatorSSLProps);
    server = cluster.startServerVM(1, serverSSLProps, locator.getPort());

    // create region
    server.invoke(CacheServerHostNameVerificationDistributedTest::createServerRegion);
    locator.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // setup client
    setupClient(clientSSLProps, server.getPort(), server.getVM().getHost().getHostName());
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
    client.invoke(CacheServerHostNameVerificationDistributedTest::createClientRegion);
  }

  private static void createClientRegion() {
    ClientRegionFactory<String, String> regionFactory =
        ClusterStartupRule.getClientCache().createClientRegionFactory(ClientRegionShortcut.PROXY);
    Region<String, String> region = regionFactory.create("region");
    assertThat(region).isNotNull();
  }

  @Test
  public void testClientSSLConnection() {
    client.invoke(CacheServerHostNameVerificationDistributedTest::doClientRegionTest);
    server.invoke(CacheServerHostNameVerificationDistributedTest::doServerRegionTest);
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
