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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import org.apache.geode.cache.ssl.ClusterSSLProvider;
import org.apache.geode.cache.ssl.TestSSLUtils.CertificateBuilder;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;

@Category({ClientServerTest.class})
public class GfshHostNameVerficationDistributedTest {
  private static MemberVM locator;

  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();

  @ClassRule
  public static GfshCommandRule gfsh = new GfshCommandRule();

  @ClassRule
  public static TemporaryFolder temporaryFolder = new TemporaryFolder();

  @BeforeClass
  public static void setupCluster() throws Exception {
    CertificateBuilder locatorCertificate = new CertificateBuilder()
        .commonName("locator")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
        .sanIpAddress(InetAddress.getLocalHost());

    CertificateBuilder gfshCertificate = new CertificateBuilder()
        .commonName("gfsh");

    ClusterSSLProvider sslProvider = new ClusterSSLProvider();

    sslProvider
        .locatorCertificate(locatorCertificate)
        .clientCertificate(gfshCertificate);

    Properties locatorSSLProps = sslProvider.locatorPropertiesWith(ALL, "any", "any");
    Properties clientSSLProps = sslProvider.clientPropertiesWith(ALL, "any", "any");

    // create a cluster
    locator = cluster.startLocatorVM(0, locatorSSLProps);

    // connect gfsh
    File sslConfigFile = gfshSecurityProperties(clientSSLProps);
    gfsh.connectAndVerify(locator.getPort(), GfshCommandRule.PortType.locator,
        "security-properties-file", sslConfigFile.getAbsolutePath());
  }

  private static File gfshSecurityProperties(Properties clientSSLProps) throws IOException {
    File sslConfigFile = temporaryFolder.newFile("gfsh-ssl.properties");
    FileOutputStream out = new FileOutputStream(sslConfigFile);
    clientSSLProps.store(out, null);
    return sslConfigFile;
  }

  @Test
  public void testGfshSSLConnection() {
    gfsh.executeAndAssertThat("list members").statusIsSuccess();
  }
}
