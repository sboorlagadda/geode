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
package org.apache.geode.internal.tcp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.ssl.CertStores;
import org.apache.geode.cache.ssl.TestSSLUtils.CertificateBuilder;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.dunit.IgnoredException;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.GfshTest;

@Category({GfshTest.class})
public class P2PHostNameVerificationDistributedTest {
  private static MemberVM locator;

  @Rule
  public ClusterStartupRule cluster = new ClusterStartupRule();

  private CertStores locatorStore, server1Store, server2Store;

  private static final String REGION = "test";

  @Test
  public void testLocatorFailsToValidateServerCertificateWithNoSANEntry() throws Exception {
    CertificateBuilder locatorCertificate = new CertificateBuilder()
        .commonName("locator")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
        .sanIpAddress(InetAddress.getLocalHost())
        .sanIpAddress(InetAddress.getByName("0.0.0.0"));

    CertificateBuilder server1Certificate = new CertificateBuilder()
        .commonName("server1");

    locatorStore = CertStores.locatorStore();
    locatorStore.withCertificate(locatorCertificate);

    server1Store = CertStores.serverStore();
    server1Store.withCertificate(server1Certificate);

    locatorStore
        .trustSelf()
        .trust("server1", server1Store.certificate());

    server1Store
        .trust("locator", locatorStore.certificate());

    Properties locatorSSLProps = locatorStore.propertiesWith("locator,server,cluster", false, true);
    Properties server1SSLProps = server1Store.propertiesWith("locator,server,cluster", false, true);

    // create a cluster
    locator = cluster.startLocatorVM(0, locatorSSLProps);
    
    IgnoredException.addIgnoredException("javax.net.ssl.SSLHandshakeException");
    IgnoredException.addIgnoredException("java.security.cert.CertificateException");
    MemberVM server1 = cluster.startServerVM(1, server1SSLProps, locator.getPort());
  }

  @Test
  public void testLocatorsAndServersCanTalkToEachOther() throws Exception {
    CertificateBuilder locatorCertificate = new CertificateBuilder()
        .commonName("locator")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
        .sanIpAddress(InetAddress.getLocalHost())
        .sanIpAddress(InetAddress.getByName("0.0.0.0"));

    CertificateBuilder server1Certificate = new CertificateBuilder()
        .commonName("server1")
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertificateBuilder server2Certificate = new CertificateBuilder()
        .commonName("server2")
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    locatorStore = CertStores.locatorStore();
    locatorStore.withCertificate(locatorCertificate);

    server1Store = CertStores.serverStore();
    server1Store.withCertificate(server1Certificate);

    server2Store = CertStores.serverStore();
    server2Store.withCertificate(server2Certificate);

    locatorStore
        .trustSelf()
        .trust("server1", server1Store.certificate())
        .trust("server2", server2Store.certificate());

    server1Store
        .trust("locator", locatorStore.certificate())
        .trust("server2", server2Store.certificate());

    server2Store
        .trust("locator", locatorStore.certificate())
        .trust("server1", server1Store.certificate());

    Properties locatorSSLProps = locatorStore.propertiesWith("locator,server,cluster", false, true);
    Properties server1SSLProps = server1Store.propertiesWith("locator,server,cluster", false, true);
    Properties server2SSLProps = server2Store.propertiesWith("locator,server,cluster", false, true);

    // create a cluster
    locator = cluster.startLocatorVM(0, locatorSSLProps);

    MemberVM server1 = cluster.startServerVM(1, server1SSLProps, locator.getPort());
    MemberVM server2 = cluster.startServerVM(2, server2SSLProps, locator.getPort());

    server1.invoke(P2PHostNameVerificationDistributedTest::createRegion);
    server2.invoke(P2PHostNameVerificationDistributedTest::createRegion);
    locator.waitUntilRegionIsReadyOnExactlyThisManyServers("/" + REGION, 2);

    // validate data put on server1 is seen on server2
    putDataOnServer(server1, "1", "one");
    validateDataOnServer(server2, "1", "one");

    // validate data put on server2 is seen on server1
    putDataOnServer(server2, "2", "two");
    validateDataOnServer(server1, "2", "two");
  }

  private static void createRegion() {
    InternalCache cache = ClusterStartupRule.getCache();
    RegionFactory regionFactory =
        cache.createRegionFactory(RegionShortcut.REPLICATE);

    Region testRegion = regionFactory.create(REGION);
    assertNotNull(testRegion);
  }

  private static void putDataOnServer(MemberVM server, String key, String value) {
    server.invoke(() -> {
      InternalCache cache = ClusterStartupRule.getCache();
      Region<String, String> testRegion = cache.getRegion(REGION);
      testRegion.put(key, value);
    });
  }

  private static void validateDataOnServer(MemberVM server, String key, String value) {
    server.invoke(() -> {
      InternalCache cache = ClusterStartupRule.getCache();
      Region<String, String> testRegion = cache.getRegion(REGION);
      assertThat(testRegion.get(key)).isEqualTo(value);
    });
  }
}
