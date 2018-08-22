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

package org.apache.geode.tools.pulse;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_CIPHERS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_ENABLED_COMPONENTS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_TYPE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_PROTOCOLS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_TYPE;
import static org.apache.geode.security.SecurableCommunicationChannels.ALL;
import static org.apache.geode.util.test.TestUtil.getResourcePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.ssl.CertStores;
import org.apache.geode.cache.ssl.TestSSLUtils;
import org.apache.geode.management.ManagementService;
import org.apache.geode.security.SecurableCommunicationChannels;
import org.apache.geode.security.SimpleTestSecurityManager;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.PulseTest;
import org.apache.geode.test.junit.categories.SecurityTest;
import org.apache.geode.test.junit.rules.EmbeddedPulseRule;
import org.apache.geode.test.junit.rules.GeodeHttpClientRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;
import org.apache.geode.tools.pulse.internal.data.Cluster;


@Category({SecurityTest.class, PulseTest.class})
public class PulseSSLHostnameVerificationTest {

  @Rule
  public LocatorStarterRule locator = new LocatorStarterRule().withHttpService();

  @Rule
  public EmbeddedPulseRule pulse = new EmbeddedPulseRule();

  private CertStores locatorStore;

  @Before
  public void beforeClass() throws Exception {
    TestSSLUtils.CertificateBuilder locatorCertificate = new TestSSLUtils.CertificateBuilder()
        .commonName("locator")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    locatorStore = CertStores.locatorStore();
    locatorStore.withCertificate(locatorCertificate);
    locatorStore.trustSelf();
  }

  @Test
  public void getAttributesWithSecurityManager() throws Exception {
    Properties locatorSSLProps = locatorStore.propertiesWith(ALL);
    locator.withProperties(locatorSSLProps).startLocator();

    pulse.setJmxSSL(true);
    pulse.useJmxPort(locator.getJmxPort());

    // set SSL props to EmbeddedPulseRule.repository
    // note this repository ref is different from jetty container's ref
    // so we need to set again in the rule
    Properties sslProps = new Properties();
    sslProps.setProperty("javax.net.ssl.trustStorePassword", locatorSSLProps.getProperty(SSL_TRUSTSTORE_PASSWORD));
    sslProps.setProperty("javax.net.ssl.trustStoreType", locatorSSLProps.getProperty(SSL_TRUSTSTORE_TYPE));
    sslProps.setProperty("javax.net.ssl.trustStore", locatorSSLProps.getProperty(SSL_TRUSTSTORE));
    sslProps.setProperty("javax.net.ssl.keyStorePassword", locatorSSLProps.getProperty(SSL_KEYSTORE_PASSWORD));
    sslProps.setProperty("javax.net.ssl.keyStoreType", locatorSSLProps.getProperty(SSL_KEYSTORE_TYPE));
    sslProps.setProperty("javax.net.ssl.keyStore", locatorSSLProps.getProperty(SSL_KEYSTORE));
    pulse.getRepository().setJavaSslProperties(sslProps);

    ManagementService service =
        ManagementService.getExistingManagementService(locator.getCache());

    await().atMost(2, MINUTES)
        .untilAsserted(() -> assertThat(service.getMemberMXBean()).isNotNull());

    Cluster cluster = pulse.getRepository().getCluster("cluster", "cluster");
    Cluster.Member[] members = cluster.getMembers();
    assertThat(members.length).isEqualTo(1);
    assertThat(members[0].getName()).isEqualTo("locator");
  }
}
