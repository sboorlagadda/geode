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

import org.apache.geode.cache.Region;
import org.apache.geode.test.dunit.rules.CleanupDUnitVMsRule;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.apache.geode.distributed.ConfigurationProperties.*;
import static org.assertj.core.api.Assertions.assertThat;

@Category({GfshTest.class})
public class GfshCredHubCertificateDistributedTest {
  @ClassRule
  public static CleanupDUnitVMsRule cleanupDUnitVMsRule = new CleanupDUnitVMsRule();

  private static MemberVM locator;

  @Rule
  public ClusterStartupRule cluster = new ClusterStartupRule();

  @Rule
  public GfshCommandRule gfsh = new GfshCommandRule();

  private static final File trustStore;
  private static final File keyStore;

  static {
    trustStore = new File("/Users/pivotal/workspace/debug-ssl/truststore.jks");
    keyStore = new File("/Users/pivotal/workspace/debug-ssl/keystore.jks");
  }

  private static final Properties gfshSSLProps = new Properties() {
    {
      setProperty(SSL_ENABLED_COMPONENTS, "web,jmx,locator,server,cluster");
      setProperty(SSL_KEYSTORE, keyStore.getAbsolutePath());
      setProperty(SSL_KEYSTORE_PASSWORD, "eI0LO551WnHDG1VLwZlljGv0XDkTYh");
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_TRUSTSTORE, trustStore.getAbsolutePath());
      setProperty(SSL_TRUSTSTORE_PASSWORD, "I6XYYtTmlmaGe9YkmORKtZROWZAFmy");
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
      setProperty(SSL_ENDPOINT_IDENTIFICATION_ENABLED, "true");
    }
  };

  private static final Properties locatorSSLProps = new Properties() {
    {
      setProperty("bind-address", locatorDns);
      setProperty(SSL_ENABLED_COMPONENTS, "web,jmx,locator,server,cluster");
      setProperty(SSL_KEYSTORE, keyStore.getAbsolutePath());
      setProperty(SSL_KEYSTORE_PASSWORD, "eI0LO551WnHDG1VLwZlljGv0XDkTYh");
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_TRUSTSTORE, trustStore.getAbsolutePath());
      setProperty(SSL_TRUSTSTORE_PASSWORD, "I6XYYtTmlmaGe9YkmORKtZROWZAFmy");
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
      setProperty(SSL_ENDPOINT_IDENTIFICATION_ENABLED, "true");
    }
  };

  private static final Properties server1SSLProps = new Properties() {
    {
      setProperty("bind-address", server1Dns);
      //setProperty("log-level", "trace");
      setProperty(SSL_ENABLED_COMPONENTS, "web,jmx,locator,server,cluster");
      setProperty(SSL_KEYSTORE, keyStore.getAbsolutePath());
      setProperty(SSL_KEYSTORE_PASSWORD, "eI0LO551WnHDG1VLwZlljGv0XDkTYh");
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_TRUSTSTORE, trustStore.getAbsolutePath());
      setProperty(SSL_TRUSTSTORE_PASSWORD, "I6XYYtTmlmaGe9YkmORKtZROWZAFmy");
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
      setProperty(SSL_ENDPOINT_IDENTIFICATION_ENABLED, "true");
    }
  };

  private static final Properties server2SSLProps = new Properties() {
    {
      setProperty("bind-address", server2Dns);
      setProperty(SSL_ENABLED_COMPONENTS, "web,jmx,locator,server,cluster");
      setProperty(SSL_KEYSTORE, keyStore.getAbsolutePath());
      setProperty(SSL_KEYSTORE_PASSWORD, "eI0LO551WnHDG1VLwZlljGv0XDkTYh");
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_TRUSTSTORE, trustStore.getAbsolutePath());
      setProperty(SSL_TRUSTSTORE_PASSWORD, "I6XYYtTmlmaGe9YkmORKtZROWZAFmy");
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
      setProperty(SSL_ENDPOINT_IDENTIFICATION_ENABLED, "true");
    }
  };

  private static final String locatorDns = "fd0a7fd9-9ef2-4e6c-b47c-86374f46d598.locator-server.quincy-services-subnet.service-instance-e0e7a946-de3c-4c09-ab6d-8722354fc8f7.bosh";
  private static final String server1Dns = "fd0a7fd9-9ef2-4e6c-b47c-86374f46d598.locator-server.quincy-services-subnet.service-instance-e0e7a946-de3c-4c09-ab6d-8722354fc8f7.bosh";
  private static final String server2Dns = "4b0ac38e-68c6-4839-8f28-f6ac075adb04.server.quincy-services-subnet.service-instance-e0e7a946-de3c-4c09-ab6d-8722354fc8f7.bosh";

  private File gfshSecurityProperties(Properties gfshSSLProps) throws IOException {
    File sslConfigFile = File.createTempFile("gfsh-ssl", "properties");
    FileOutputStream out = new FileOutputStream(sslConfigFile);
    gfshSSLProps.store(out, null);
    return sslConfigFile;
  }

  @Test
  public void gfshConnectsToLocator() throws Exception {
    // create a cluster
    locator = cluster.startLocatorVM(0, l -> l
      .withPort(55221)
      .withSystemProperty("gemfire.locators", locatorDns + "[55221]")
      .withSystemProperty("gpfdist-hostname", locatorDns)
      .withSystemProperty("gemfire.forceDnsUse", "true")
            .withSystemProperty("jdk.tls.trustNameService", "true")
      .withProperties(locatorSSLProps)
    );

    MemberVM server = cluster.startServerVM(1, cacheRule -> cacheRule
            .withSystemProperty("gemfire.locators", locatorDns+"[55221]")
            .withSystemProperty("jdk.tls.trustNameService", "true")
            .withSystemProperty("gemfire.forceDnsUse", "true")
            .withProperties(server1SSLProps));

    MemberVM server2 = cluster.startServerVM(2, cacheRule -> cacheRule
            .withSystemProperty("gemfire.locators", locatorDns + "[55221]")
            .withSystemProperty("jdk.tls.trustNameService", "true")
            .withSystemProperty("gemfire.forceDnsUse", "true")
            .withProperties(server2SSLProps));

    // connect gfsh
    File sslConfigFile = gfshSecurityProperties(gfshSSLProps);
    gfsh.connectToHostAndVerify(locatorDns, locator.getPort(), GfshCommandRule.PortType.locator,
        "security-properties-file", sslConfigFile.getAbsolutePath());

    gfsh.executeAndAssertThat("list members").statusIsSuccess();
    gfsh.executeAndAssertThat("create region --name=/test --type=REPLICATE").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='1' --value='one'").statusIsSuccess();
    gfsh.executeAndAssertThat("put --region=/test --key='2' --value='two'").statusIsSuccess();

    server.invoke(() -> {
      Region<Object, Object> region = ClusterStartupRule.getCache().getRegion("/test");
      region.put("3", "three");
    });

    server2.invoke(() -> {
      Region<Object, Object> region = ClusterStartupRule.getCache().getRegion("/test");
      String value = (String) region.get("3");
      ClusterStartupRule.getCache().getLogger().info(">>>>>>>> SAI>>>>>>>> Value:"+value);
      assertThat(value).isEqualTo("three");
    });
  }
}
