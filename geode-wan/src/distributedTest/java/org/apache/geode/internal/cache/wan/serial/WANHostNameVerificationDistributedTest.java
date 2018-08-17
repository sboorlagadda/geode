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
package org.apache.geode.internal.cache.wan.serial;

import static org.apache.geode.distributed.ConfigurationProperties.DISTRIBUTED_SYSTEM_ID;
import static org.apache.geode.distributed.ConfigurationProperties.MCAST_PORT;
import static org.apache.geode.distributed.ConfigurationProperties.REMOTE_LOCATORS;
import static org.apache.geode.security.SecurableCommunicationChannels.ALL;
import static org.apache.geode.test.dunit.rules.ClusterStartupRule.getCache;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.awaitility.Duration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.ssl.CertStores;
import org.apache.geode.cache.ssl.TestSSLUtils;
import org.apache.geode.cache.wan.GatewayReceiverFactory;
import org.apache.geode.cache.wan.GatewaySenderFactory;
import org.apache.geode.internal.AvailablePortHelper;
import org.apache.geode.test.dunit.IgnoredException;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.WanTest;

@Category({WanTest.class})
public class WANHostNameVerificationDistributedTest {

  private static MemberVM locator_s1;
  private static MemberVM server_s1;

  private static MemberVM locator_s2;
  private static MemberVM server_s2;

  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();

  @BeforeClass
  public static void setupCluster() throws Exception {
    IgnoredException.addIgnoredException("Connection reset");
    IgnoredException.addIgnoredException("Broken pipe");
    IgnoredException.addIgnoredException("Connection refused");
    IgnoredException.addIgnoredException("could not get remote locator information");
    IgnoredException.addIgnoredException("Unexpected IOException");

    TestSSLUtils.CertificateBuilder site1_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("site1")
        // ClusterStartupRule uses 'localhost' as locator host
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    TestSSLUtils.CertificateBuilder site2_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("site2")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertStores site1_stores = new CertStores("site1", "site1");
    site1_stores.withCertificate(site1_cert);

    CertStores site2_stores = new CertStores("site2", "site2");
    site2_stores.withCertificate(site2_cert);

    int site1Port = setupWanSite1(site1_stores, site2_stores);
    setupWanSite2(site1Port, site2_stores, site1_stores);
  }

  private static int setupWanSite1(CertStores site1_stores, CertStores site2_stores)
      throws GeneralSecurityException, IOException {

    Properties site1_props = site1_stores
        .trustSelf()
        .trust(site2_stores.alias(), site2_stores.certificate())
        .propertiesWith(ALL);

    // create a cluster
    site1_props.setProperty(MCAST_PORT, "0");
    site1_props.setProperty(DISTRIBUTED_SYSTEM_ID, "1");
    locator_s1 = cluster.startLocatorVM(0, site1_props);
    server_s1 = cluster.startServerVM(1, site1_props, locator_s1.getPort());

    // create a region
    server_s1.invoke(WANHostNameVerificationDistributedTest::createSite1Region);
    locator_s1.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // create gateway sender
    server_s1.invoke(WANHostNameVerificationDistributedTest::createGatewaySender);
    locator_s1.waitUntilGatewaySendersAreReadyOnExactlyThisManyServers(1);

    return locator_s1.getPort();
  }

  private static void setupWanSite2(int site1Port, CertStores site2_stores, CertStores site1_stores)
      throws GeneralSecurityException, IOException {

    Properties site2_props = site2_stores
        .trustSelf()
        .trust(site1_stores.alias(), site1_stores.certificate())
        .propertiesWith(ALL);

    // create a cluster
    site2_props.setProperty(MCAST_PORT, "0");
    site2_props.setProperty(DISTRIBUTED_SYSTEM_ID, "2");
    site2_props.setProperty(REMOTE_LOCATORS, "localhost[" + site1Port + "]");

    locator_s2 = cluster.startLocatorVM(2, site2_props);
    server_s2 = cluster.startServerVM(3, site2_props, locator_s2.getPort());

    // create a region
    server_s2.invoke(WANHostNameVerificationDistributedTest::createSite2Region);
    locator_s2.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // create gateway sender
    server_s2.invoke(WANHostNameVerificationDistributedTest::createGatewayReceiver);
  }

  private static void createGatewayReceiver() {
    int port = AvailablePortHelper.getRandomAvailablePortForDUnitSite();
    GatewayReceiverFactory gwReceiver = getCache().createGatewayReceiverFactory();
    gwReceiver.setStartPort(port);
    gwReceiver.setEndPort(port);
    gwReceiver.create();
  }

  private static void createGatewaySender() {
    GatewaySenderFactory gwSender = getCache().createGatewaySenderFactory();
    gwSender.setBatchSize(1);
    gwSender.create("s1", 2);
  }

  private static void createSite1Region() {
    RegionFactory factory =
        getCache().createRegionFactory(RegionShortcut.REPLICATE);
    factory.addGatewaySenderId("s1");
    factory.create("region");
  }

  private static void createSite2Region() {
    RegionFactory factory =
        getCache().createRegionFactory(RegionShortcut.REPLICATE);
    factory.create("region");
  }

  private static void doPutOnSite1() {
    Region<String, String> region = getCache().getRegion("region");
    region.put("serverkey", "servervalue");
    assertThat("servervalue").isEqualTo(region.get("serverkey"));
  }

  private static void verifySite2Received() {
    Region<String, String> region = getCache().getRegion("region");
    await()
        .atMost(Duration.FIVE_SECONDS)
        .pollInterval(Duration.ONE_SECOND)
        .until(() -> assertThat("servervalue").isEqualTo(region.get("serverkey")));
  }

  @Test
  public void testWANSSL() {
    server_s1.invoke(WANHostNameVerificationDistributedTest::doPutOnSite1);
    server_s2.invoke(WANHostNameVerificationDistributedTest::verifySite2Received);
  }
}
