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
package org.apache.geode.management.internal.cli.commands;

import org.apache.geode.cache.ssl.CertStores;
import org.apache.geode.cache.ssl.TestSSLUtils.CertificateBuilder;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

@Category({GfshTest.class})
public class GfshSSLValidationDistributedTest {
  private static MemberVM locator;

  @Rule
  public ClusterStartupRule cluster = new ClusterStartupRule();

  @Rule
  public GfshCommandRule gfsh = new GfshCommandRule();

  private CertStores locatorStore, serverStore;
  private CertStores gfshStore;

  @Before
  public void setupCluster() throws Exception {
    CertificateBuilder locatorServerCertificate = new CertificateBuilder()
        .commonName("locator-server")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
            .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
            .sanIpAddress(InetAddress.getLocalHost())
        .sanIpAddress(InetAddress.getByName("0.0.0.0"));

    CertificateBuilder serverCertificate = new CertificateBuilder()
            .commonName("server")
            .sanDnsName(InetAddress.getLocalHost().getHostName())
            .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
            .sanIpAddress(InetAddress.getLocalHost());

    CertificateBuilder gfshCertificate = new CertificateBuilder()
        .commonName("gfsh");

    locatorStore = CertStores.locatorStore();
    locatorStore.withCertificate(locatorServerCertificate);

    serverStore = CertStores.serverStore();
    serverStore.withCertificate(serverCertificate);

    gfshStore = CertStores.clientStore();
    gfshStore.withCertificate(gfshCertificate);

    locatorStore.trustSelf()
                .trust(serverStore.alias(), serverStore.certificate());

    serverStore.trust(locatorStore.alias(), locatorStore.certificate());

    gfshStore
        .trust(locatorStore.alias(), locatorStore.certificate());
  }

  private File gfshSecurityProperties(Properties clientSSLProps) throws IOException {
    File sslConfigFile = File.createTempFile("gfsh-ssl", "properties");
    FileOutputStream out = new FileOutputStream(sslConfigFile);
    clientSSLProps.store(out, null);
    return sslConfigFile;
  }

  @Test
  public void gfshConnectsToLocator() throws Exception {
    Properties locatorSSLProps = locatorStore.propertiesWith("web,jmx,locator,server,cluster", false, true);
    Properties serverSSLProps = serverStore.propertiesWith("web,jmx,locator,server,cluster", false, true);
    Properties gfshSSLProps = gfshStore.propertiesWith("web,jmx,locator,server,cluster", false, true);

    // create a cluster
    locator = cluster.startLocatorVM(0, l -> l
      .withPort(55221)
      .withSystemProperty("gemfire.locators", "localhost[55221]")
      .withSystemProperty("gpfdist-hostname", "localhost")
      .withSystemProperty("gemfire.forceDnsUse", "true")
      .withProperties(locatorSSLProps)
    );

    MemberVM server = cluster.startServerVM(1, cacheRule -> cacheRule
            .withSystemProperty("gemfire.locators", "localhost[55221]")
            .withProperties(serverSSLProps));

    // connect gfsh
    File sslConfigFile = gfshSecurityProperties(gfshSSLProps);
    gfsh.connectToHostAndVerify("localhost", locator.getPort(), GfshCommandRule.PortType.locator,
        "security-properties-file", sslConfigFile.getAbsolutePath());

    gfsh.executeAndAssertThat("list members").statusIsSuccess();
    gfsh.executeAndAssertThat("create region --name=/test --type=REPLICATE").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='1' --value='one'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='2' --value='two'").statusIsSuccess();
  }
}
