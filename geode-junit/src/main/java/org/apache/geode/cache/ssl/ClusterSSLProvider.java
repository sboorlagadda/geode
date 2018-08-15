package org.apache.geode.cache.ssl;

import static org.apache.geode.cache.ssl.TestSSLUtils.createKeyStore;
import static org.apache.geode.cache.ssl.TestSSLUtils.createTrustStore;
import static org.apache.geode.cache.ssl.TestSSLUtils.generateKeyPair;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_CIPHERS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_ENABLED_COMPONENTS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_TYPE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_PROTOCOLS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_TYPE;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClusterSSLProvider {

  private Map<String, KeyPair> keyPairs = new HashMap<>();
  private Map<String, X509Certificate> certs = new HashMap<>();

  private File serverKeyStoreFile;
  private File clientKeyStoreFile;

  public ClusterSSLProvider serverCertificate(TestSSLUtils.CertificateBuilder certificateBuilder)
      throws GeneralSecurityException, IOException {
    serverKeyStoreFile = File.createTempFile("serverKS", ".jks");
    certificate("server", certificateBuilder, serverKeyStoreFile);
    return this;
  }

  public ClusterSSLProvider clientCertificate(
      TestSSLUtils.CertificateBuilder certificateBuilder)
      throws GeneralSecurityException, IOException {
    clientKeyStoreFile = File.createTempFile("clientKS", ".jks");
    certificate("client", certificateBuilder, clientKeyStoreFile);
    return this;
  }

  private void certificate(String alias, TestSSLUtils.CertificateBuilder certificateBuilder,
      File keyStoreFile) throws GeneralSecurityException, IOException {
    KeyPair keyPair = generateKeyPair("RSA");
    keyPairs.put(alias, keyPair);

    X509Certificate cert = certificateBuilder.generate(keyPair);
    certs.put(alias, cert);

    createKeyStore(keyStoreFile.getPath(), "password", alias, keyPair.getPrivate(), cert);
  }

  public Properties generateServerPropertiesWith(String components, String protocols,
      String ciphers)
      throws GeneralSecurityException, IOException {
    File serverTrustStoreFile = File.createTempFile("serverTS", ".jks");
    serverTrustStoreFile.deleteOnExit();

    // a server should trust itself, locator and client
    createTrustStore(serverTrustStoreFile.getPath(), "password", certs);

    return generatePropertiesWith(components, protocols, ciphers, serverTrustStoreFile,
        serverKeyStoreFile);
  }

  public Properties generateClientPropertiesWith(String components, String protocols,
      String ciphers)
      throws GeneralSecurityException, IOException {
    File clientTrustStoreFile = File.createTempFile("clientTS", ".jks");
    clientTrustStoreFile.deleteOnExit();

    // only trust locator and server
    Map<String, X509Certificate> trustedCerts = new HashMap<>();
    trustedCerts.put("server", certs.get("server"));

    createTrustStore(clientTrustStoreFile.getPath(), "password", trustedCerts);

    return generatePropertiesWith(components, protocols, ciphers, clientTrustStoreFile,
        clientKeyStoreFile);
  }

  private Properties generatePropertiesWith(String components, String protocols, String ciphers,
      File trustStoreFile, File keyStoreFile) {

    Properties sslConfigs = new Properties();
    sslConfigs.setProperty(SSL_ENABLED_COMPONENTS, components);
    sslConfigs.setProperty(SSL_KEYSTORE, keyStoreFile.getPath());
    sslConfigs.setProperty(SSL_KEYSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_KEYSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE, trustStoreFile.getPath());
    sslConfigs.setProperty(SSL_TRUSTSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_PROTOCOLS, protocols);
    sslConfigs.setProperty(SSL_CIPHERS, ciphers);

    return sslConfigs;
  }
}
