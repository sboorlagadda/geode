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
import org.apache.geode.test.dunit.IgnoredException;
import org.apache.geode.test.dunit.VM;
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

import static org.apache.geode.security.SecurableCommunicationChannels.*;

@Category({GfshTest.class})
public class GfshSSLValidationDistributedTest {
  private static MemberVM locator;

  @Rule
  public ClusterStartupRule cluster = new ClusterStartupRule();

  @Rule
  public GfshCommandRule gfsh = new GfshCommandRule();

  private CertStores locatorServerStore, server2Store;
  private CertStores gfshStore;

  @Before
  public void setupCluster() throws Exception {
    CertificateBuilder locatorServerCertificate = new CertificateBuilder()
        .commonName("gemfire-ssl")
        .sanDnsName(InetAddress.getLoopbackAddress().getHostName())
        .sanDnsName(InetAddress.getLocalHost().getHostName())
            .sanDnsName(InetAddress.getLocalHost().getCanonicalHostName())
            .sanIpAddress(InetAddress.getLocalHost())
        .sanIpAddress(InetAddress.getByName("0.0.0.0")); // to pass on windows

    CertificateBuilder server2Certificate = new CertificateBuilder()
            .commonName("server2")
            .sanDnsName("server");

    CertificateBuilder gfshCertificate = new CertificateBuilder()
        .commonName("gfsh");

    locatorServerStore = CertStores.locatorStore();
    locatorServerStore.withCertificate(locatorServerCertificate);

    server2Store = CertStores.serverStore();
    server2Store.withCertificate(server2Certificate);

    gfshStore = CertStores.clientStore();
    gfshStore.withCertificate(gfshCertificate);

    locatorServerStore.trustSelf();

    server2Store.trust(locatorServerStore.alias(), locatorServerStore.certificate());

    gfshStore
        .trust(locatorServerStore.alias(), locatorServerStore.certificate());
  }

  private File gfshSecurityProperties(Properties clientSSLProps) throws IOException {
    File sslConfigFile = File.createTempFile("gfsh-ssl", "properties");
    FileOutputStream out = new FileOutputStream(sslConfigFile);
    clientSSLProps.store(out, null);
    return sslConfigFile;
  }

  @Test
  public void gfshConnectsToLocator() throws Exception {
    Properties locatorSSLProps = locatorServerStore.propertiesWith("web,jmx,locator,server", false, true);
    Properties server1SSLProps = locatorServerStore.propertiesWith("web,jmx,locator,server", false, true);
    Properties server2SSLProps = server2Store.propertiesWith("web,jmx,locator,server", false, true);
    Properties gfshSSLProps = gfshStore.propertiesWith("web,jmx,locator,server", false, true);

    // create a cluster
    locator = cluster.startLocatorVM(0, locatorSSLProps);
    MemberVM server = cluster.startServerVM(1, server1SSLProps, locator.getPort());
    MemberVM server2 = cluster.startServerVM(2, server2SSLProps, locator.getPort());

    // connect gfsh
    File sslConfigFile = gfshSecurityProperties(gfshSSLProps);
    gfsh.connectAndVerify(locator.getPort(), GfshCommandRule.PortType.locator,
        "security-properties-file", sslConfigFile.getAbsolutePath());

    gfsh.executeAndAssertThat("list members").statusIsSuccess();
    gfsh.executeAndAssertThat("create region --name=/test --type=REPLICATE").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='1' --value='one'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='2' --value='two'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='3' --value='three'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='4' --value='four'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='5' --value='five'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='6' --value='six'").statusIsSuccess();

  }
}
