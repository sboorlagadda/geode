package org.apache.geode.cache.client.internal.provider;

import static org.apache.geode.distributed.ConfigurationProperties.SSL_CIPHERS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_ENABLED_COMPONENTS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_TYPE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_PROTOCOLS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_REQUIRE_AUTHENTICATION;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

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
import org.apache.geode.cache.client.internal.CacheServerSSLConnectionDUnitTest;
import org.apache.geode.internal.security.SecurableCommunicationChannel;
import org.apache.geode.test.dunit.SerializableConsumerIF;
import org.apache.geode.test.dunit.rules.ClientVM;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.apache.geode.util.test.TestUtil;

@Category({ClientServerTest.class})
public class CustomSSLProviderDistributedTest {
  private static MemberVM locator;
  private static MemberVM server;
  private static ClientVM client;

  private static final String CLIENT_KEY_STORE = "client.keystore";
  private static final String CLIENT_TRUST_STORE = "client.truststore";
  private static final String SERVER_KEY_STORE = "cacheserver.keystore";
  private static final String SERVER_TRUST_STORE = "cacheserver.truststore";

  @ClassRule
  public static ClusterStartupRule cluster = new ClusterStartupRule();

  @ClassRule
  public static GfshCommandRule gfsh = new GfshCommandRule();


  private static Properties getServerSecurityProperties() {
    Properties serverSecurityProps = new Properties();

    serverSecurityProps.put(SSL_ENABLED_COMPONENTS, SecurableCommunicationChannel.ALL);
    serverSecurityProps.put(SSL_PROTOCOLS, "any");
    serverSecurityProps.put(SSL_CIPHERS, "any");
    serverSecurityProps.put(SSL_REQUIRE_AUTHENTICATION, String.valueOf("true"));

    String keyStore =
        TestUtil.getResourcePath(CustomSSLProviderDistributedTest.class, SERVER_KEY_STORE);
    String trustStore =
        TestUtil.getResourcePath(CustomSSLProviderDistributedTest.class, SERVER_TRUST_STORE);
    serverSecurityProps.put(SSL_KEYSTORE_TYPE, "jks");
    serverSecurityProps.put(SSL_KEYSTORE, keyStore);
    serverSecurityProps.put(SSL_KEYSTORE_PASSWORD, "password");
    serverSecurityProps.put(SSL_TRUSTSTORE, trustStore);
    serverSecurityProps.put(SSL_TRUSTSTORE_PASSWORD, "password");

    return serverSecurityProps;
  }

  private static Properties getClientSecurityProperties() {
    Properties clientSecurityPropos = new Properties();
    String keyStorePath =
        TestUtil.getResourcePath(CacheServerSSLConnectionDUnitTest.class, CLIENT_KEY_STORE);
    String trustStorePath =
        TestUtil.getResourcePath(CacheServerSSLConnectionDUnitTest.class, CLIENT_TRUST_STORE);

    clientSecurityPropos.put(SSL_ENABLED_COMPONENTS, "server");
    clientSecurityPropos.put(SSL_CIPHERS, "any");
    clientSecurityPropos.put(SSL_PROTOCOLS, "any");
    clientSecurityPropos.put(SSL_REQUIRE_AUTHENTICATION, String.valueOf(false));

    clientSecurityPropos.put(SSL_KEYSTORE_TYPE, "jks");
    clientSecurityPropos.put(SSL_KEYSTORE, keyStorePath);
    clientSecurityPropos.put(SSL_KEYSTORE_PASSWORD, "password");
    clientSecurityPropos.put(SSL_TRUSTSTORE, trustStorePath);
    clientSecurityPropos.put(SSL_TRUSTSTORE_PASSWORD, "password");

    return clientSecurityPropos;
  }

  @BeforeClass
  public static void setupCluster() throws Exception {
    //create a cluster
    //locator = cluster.startLocatorVM(0, getServerSecurityProperties());
    //server = cluster.startServerVM(1, getServerSecurityProperties(), locator.getPort());
    locator = cluster.startLocatorVM(0);
    server = cluster.startServerVM(1, locator.getPort());

    //create region
    server.invoke(CustomSSLProviderDistributedTest::createServerRegion);
    locator.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    //setup client
    setupClient(server.getPort(), server.getVM().getHost().getHostName());
  }

  private static void createServerRegion() {
    RegionFactory factory = ClusterStartupRule.getCache().createRegionFactory(RegionShortcut.REPLICATE);
    Region r = factory.create("region");
    r.put("serverkey", "servervalue");
  }

  private static void setupClient(int serverPort, String serverHost) throws Exception {
    SerializableConsumerIF<ClientCacheFactory> clientSetup = cf -> {
      cf.addPoolServer(serverHost, serverPort);
    };

    //client = cluster.startClientVM(2, getClientSecurityProperties(), clientSetup);
    client = cluster.startClientVM(2, new Properties(), clientSetup);

    //create a client region
    client.invoke(CustomSSLProviderDistributedTest::createClientRegion);
  }

  private static void createClientRegion() {
    ClientRegionFactory<String, String> regionFactory =
        ClusterStartupRule.getClientCache().createClientRegionFactory(ClientRegionShortcut.PROXY);
    Region<String, String> region = regionFactory.create("region");
    assertThat(region).isNotNull();
  }

  @Test
  public void testClientSSLConnection() {
    client.invoke(CustomSSLProviderDistributedTest::doClientRegionTest);
    server.invoke(CustomSSLProviderDistributedTest::doServerRegionTest);
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
