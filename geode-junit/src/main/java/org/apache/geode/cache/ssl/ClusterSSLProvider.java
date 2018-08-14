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
import static org.apache.geode.security.SecurableCommunicationChannels.ALL;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ClusterSSLProvider {

  private Map<String, KeyPair> keyPairs = new HashMap<>();
  private Map<String, X509Certificate> certs = new HashMap<>();

  private File serverKeyStoreFile;
  private File clientKeyStoreFile;

  public ClusterSSLProvider withServerCertificate(String cn)
      throws GeneralSecurityException, IOException {
    this.withServerCertificate("server", new TestSSLUtils.CertificateBuilder().name(cn));
    return this;
  }

  public ClusterSSLProvider withServerCertificate(String cn, String hostname)
      throws GeneralSecurityException, IOException {
    this.withServerCertificate("server",
        new TestSSLUtils.CertificateBuilder().name(cn).sanDnsName(hostname));
    return this;
  }

  public ClusterSSLProvider withServerCertificate(String cn, InetAddress ipAddress)
      throws GeneralSecurityException, IOException {
    this.withServerCertificate("server",
        new TestSSLUtils.CertificateBuilder().name(cn).sanIpAddress(ipAddress));
    return this;
  }

  public ClusterSSLProvider withServerCertificate(String cn, List<String> hostnames,
      InetAddress hostAddress)
      throws GeneralSecurityException, IOException {
    this.withServerCertificate("server",
        new TestSSLUtils.CertificateBuilder().name(cn).sanDnsAndIpAddress(hostnames, hostAddress));
    return this;
  }

  public ClusterSSLProvider withServerCertificate(String alias,
      TestSSLUtils.CertificateBuilder certificateBuilder)
      throws GeneralSecurityException, IOException {
    KeyPair keyPair = generateKeyPair("RSA");
    keyPairs.put(alias, keyPair);

    X509Certificate cert = certificateBuilder.generate(keyPair);
    certs.put(alias, cert);

    serverKeyStoreFile = File.createTempFile("serverKS", ".jks");
    createKeyStore(serverKeyStoreFile.getPath(), "password", alias, keyPair.getPrivate(), cert);
    return this;
  }

  public ClusterSSLProvider withClientCertificate(String cn)
      throws GeneralSecurityException, IOException {
    this.withClientCertificate("client", new TestSSLUtils.CertificateBuilder().name(cn));
    return this;
  }

  public ClusterSSLProvider withClientCertificate(String alias,
      TestSSLUtils.CertificateBuilder certificateBuilder)
      throws GeneralSecurityException, IOException {
    KeyPair keyPair = generateKeyPair("RSA");
    keyPairs.put(alias, keyPair);

    X509Certificate cert = certificateBuilder.generate(keyPair);
    certs.put(alias, cert);

    clientKeyStoreFile = File.createTempFile("clientKS", ".jks");
    createKeyStore(clientKeyStoreFile.getPath(), "password", alias, keyPair.getPrivate(), cert);
    return this;
  }

  public Properties generateServerPropertiesWith()
      throws GeneralSecurityException, IOException {
    File serverTrustStoreFile = File.createTempFile("serverTS", ".jks");
    serverTrustStoreFile.deleteOnExit();

    // for peer2peer server should trust itself
    createTrustStore(serverTrustStoreFile.getPath(), "password", certs);

    Properties sslConfigs = new Properties();
    sslConfigs.setProperty(SSL_ENABLED_COMPONENTS, ALL);
    sslConfigs.setProperty(SSL_KEYSTORE, serverKeyStoreFile.getPath());
    sslConfigs.setProperty(SSL_KEYSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_KEYSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE, serverTrustStoreFile.getPath());
    sslConfigs.setProperty(SSL_TRUSTSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_PROTOCOLS, "any");
    sslConfigs.setProperty(SSL_CIPHERS, "any");

    return sslConfigs;
  }

  public Properties generateClientPropertiesWith()
      throws GeneralSecurityException, IOException {
    File clientTrustStoreFile = File.createTempFile("clientTS", ".jks");
    clientTrustStoreFile.deleteOnExit();
    // only trust server cert
    Map<String, X509Certificate> trustedCerts = new HashMap<>();
    trustedCerts.put("server", certs.get("server"));

    createTrustStore(clientTrustStoreFile.getPath(), "password", trustedCerts);

    Properties sslConfigs = new Properties();
    sslConfigs.setProperty(SSL_ENABLED_COMPONENTS, ALL);
    sslConfigs.setProperty(SSL_KEYSTORE, clientKeyStoreFile.getPath());
    sslConfigs.setProperty(SSL_KEYSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_KEYSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE, clientTrustStoreFile.getPath());
    sslConfigs.setProperty(SSL_TRUSTSTORE_PASSWORD, "password");
    sslConfigs.setProperty(SSL_TRUSTSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_PROTOCOLS, "any");
    sslConfigs.setProperty(SSL_CIPHERS, "any");

    return sslConfigs;
  }
}
