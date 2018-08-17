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

public class WANDiffKeyHostNameVerificationDistributedTest {

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

    TestSSLUtils.CertificateBuilder locator_s1_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("locator_s1")
        // ClusterStartupRule uses 'localhost' as locator host
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    TestSSLUtils.CertificateBuilder server_s1_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("server_s1")
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    TestSSLUtils.CertificateBuilder locator_s2_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("locator_s2")
        // ClusterStartupRule uses 'localhost' as locator host
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    TestSSLUtils.CertificateBuilder server_s2_cert = new TestSSLUtils.CertificateBuilder()
        .commonName("server_s2")
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertStores locator_s1_store = new CertStores("site1_locator", "site1_locator");
    locator_s1_store.withCertificate(locator_s1_cert);

    CertStores server_s1_store = new CertStores("site1_server", "site1_server");
    server_s1_store.withCertificate(server_s1_cert);

    CertStores locator_s2_store = new CertStores("site2_locator", "site2_locator");
    locator_s2_store.withCertificate(locator_s2_cert);

    CertStores server_s2_store = new CertStores("site2_server", "site2_server");
    server_s2_store.withCertificate(server_s2_cert);

    int site1Port =
        setupWanSite1(locator_s1_store, server_s1_store, locator_s2_store, server_s2_store);
    setupWanSite2(site1Port, locator_s2_store, server_s2_store, locator_s1_store, server_s1_store);
  }

  private static int setupWanSite1(CertStores locator_s1_store, CertStores server_s1_store,
      CertStores locator_s2_store, CertStores server_s2_store)
      throws GeneralSecurityException, IOException {

    Properties locatorSSLProps = locator_s1_store
        .trustSelf()
        .trust(server_s1_store.alias(), server_s1_store.certificate())
        .trust(locator_s2_store.alias(), locator_s2_store.certificate())
        .propertiesWith(ALL);

    Properties serverSSLProps = server_s1_store
        .trustSelf()
        .trust(locator_s1_store.alias(), locator_s1_store.certificate())
        .trust(server_s2_store.alias(), server_s2_store.certificate())
        .propertiesWith(ALL);

    // create a cluster
    locatorSSLProps.setProperty(DISTRIBUTED_SYSTEM_ID, "1");
    locator_s1 = cluster.startLocatorVM(0, locatorSSLProps);
    server_s1 = cluster.startServerVM(1, serverSSLProps, locator_s1.getPort());

    // create a region
    server_s1.invoke(WANDiffKeyHostNameVerificationDistributedTest::createServerRegion);
    locator_s1.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // create gateway sender
    server_s1.invoke(WANDiffKeyHostNameVerificationDistributedTest::createGatewaySender);

    return locator_s1.getPort();
  }

  private static void setupWanSite2(int site1Port, CertStores locator_s2_store,
      CertStores server_s2_store,
      CertStores locator_s1_store, CertStores server_s1_store)
      throws GeneralSecurityException, IOException {

    Properties locator_s2_props = locator_s2_store
        .trustSelf()
        .trust(server_s2_store.alias(), server_s2_store.certificate())
        .trust(locator_s1_store.alias(), locator_s1_store.certificate())
        .propertiesWith(ALL);

    locator_s2_props.setProperty(MCAST_PORT, "0");
    locator_s2_props.setProperty(DISTRIBUTED_SYSTEM_ID, "2");
    locator_s2_props.setProperty(REMOTE_LOCATORS, "localhost[" + site1Port + "]");

    Properties server_s2_props = server_s2_store
        .trustSelf()
        .trust(locator_s2_store.alias(), locator_s2_store.certificate())
        .trust(server_s1_store.alias(), server_s1_store.certificate())
        .propertiesWith(ALL);

    // create a cluster
    locator_s2_props.setProperty(DISTRIBUTED_SYSTEM_ID, "2");
    locator_s2 = cluster.startLocatorVM(2, locator_s2_props);
    server_s2 = cluster.startServerVM(3, server_s2_props, locator_s2.getPort());

    // create a region
    server_s2.invoke(WANDiffKeyHostNameVerificationDistributedTest::createServerRegion);
    locator_s2.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    // create gateway sender
    server_s2.invoke(WANDiffKeyHostNameVerificationDistributedTest::createGatewayReceiver);
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

  private static void createServerRegion() {
    RegionFactory factory =
        ClusterStartupRule.getCache().createRegionFactory(RegionShortcut.REPLICATE);
    factory.addGatewaySenderId("s1");
    factory.create("region");
  }

  private static void doPutOnSite1() {
    Region<String, String> region = ClusterStartupRule.getCache().getRegion("region");
    region.put("serverkey", "servervalue");
    assertThat("servervalue").isEqualTo(region.get("serverkey"));
  }

  private static void verifySite2Received() {
    Region<String, String> region = ClusterStartupRule.getCache().getRegion("region");
    await()
        .atMost(Duration.FIVE_SECONDS)
        .pollInterval(Duration.ONE_SECOND)
        .until(() -> assertThat("servervalue").isEqualTo(region.get("serverkey")));
  }

  @Test
  public void testWANSSL() {
    server_s1.invoke(WANDiffKeyHostNameVerificationDistributedTest::doPutOnSite1);
    server_s2.invoke(WANDiffKeyHostNameVerificationDistributedTest::verifySite2Received);
  }
}
