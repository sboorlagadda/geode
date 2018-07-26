package org.apache.geode.cache.client.internal.provider;

import static org.apache.geode.distributed.ConfigurationProperties.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.apache.geode.security.SecurableCommunicationChannels;
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

  private static String serverKeystore =
          TestUtil.getResourcePath(CustomSSLProviderDistributedTest.class, SERVER_KEY_STORE);
  private static String serverTruststore =
          TestUtil.getResourcePath(CustomSSLProviderDistributedTest.class, SERVER_TRUST_STORE);

  private static String clientKeystore =
          TestUtil.getResourcePath(CacheServerSSLConnectionDUnitTest.class, CLIENT_KEY_STORE);
  private static String clientTruststore =
          TestUtil.getResourcePath(CacheServerSSLConnectionDUnitTest.class, CLIENT_TRUST_STORE);


  private static Properties serverSSLProperties = new Properties() {
    {
      setProperty(SSL_ENABLED_COMPONENTS, SecurableCommunicationChannels.ALL);
      setProperty(SSL_KEYSTORE, serverKeystore);
      setProperty(SSL_KEYSTORE_PASSWORD, "password");
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_TRUSTSTORE, serverTruststore);
      setProperty(SSL_TRUSTSTORE_PASSWORD, "password");
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
    }
  };

  private static Properties clientSSLProperties = new Properties() {
    {

      setProperty(SSL_ENABLED_COMPONENTS, "server");
      setProperty(SSL_CIPHERS, "any");
      setProperty(SSL_PROTOCOLS, "any");
      setProperty(SSL_REQUIRE_AUTHENTICATION, String.valueOf(false));
      setProperty(SSL_KEYSTORE_TYPE, "JKS");
      setProperty(SSL_KEYSTORE, clientKeystore);
      setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
      setProperty(SSL_KEYSTORE_PASSWORD, "password");
      setProperty(SSL_TRUSTSTORE, clientTruststore);
      setProperty(SSL_TRUSTSTORE_PASSWORD, "password");
    }
  };

  @BeforeClass
  public static void setupCluster() throws Exception {
    //create a cluster
    locator = cluster.startLocatorVM(0, serverSSLProperties);
    server = cluster.startServerVM(1, serverSSLProperties, locator.getPort());
    //locator = cluster.startLocatorVM(0);
    //server = cluster.startServerVM(1, locator.getPort());

    //create region
    server.invoke(CustomSSLProviderDistributedTest::createServerRegion);
    locator.waitUntilRegionIsReadyOnExactlyThisManyServers("/region", 1);

    //gfsh.connect(locator);
    //gfsh.executeAndAssertThat("list regions").statusIsSuccess();

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

    client = cluster.startClientVM(2, clientSSLProperties, clientSetup);
    //client = cluster.startClientVM(2, new Properties(), clientSetup);

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
