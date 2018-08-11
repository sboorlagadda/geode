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
package org.apache.geode.cache.client.internal.security;

import static org.apache.geode.distributed.ConfigurationProperties.SSL_CIPHERS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_KEYSTORE_TYPE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_PROTOCOLS;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD;
import static org.apache.geode.distributed.ConfigurationProperties.SSL_TRUSTSTORE_TYPE;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;

public class TestSSLUtils {

  public static KeyPair generateKeyPair(String algorithm) throws NoSuchAlgorithmException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
    keyGen.initialize(1024);
    return keyGen.genKeyPair();
  }

  private static KeyStore createEmptyKeyStore() throws GeneralSecurityException, IOException {
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(null, null); // initialize
    return ks;
  }

  public static void createKeyStore(String filename,
      String password, String alias,
      Key privateKey, Certificate cert) throws GeneralSecurityException, IOException {
    KeyStore ks = createEmptyKeyStore();
    ks.setKeyEntry(alias, privateKey, password.toCharArray(), new Certificate[] {cert});
    try (OutputStream out = Files.newOutputStream(Paths.get(filename))) {
      ks.store(out, password.toCharArray());
    }
  }

  public static <T extends Certificate> void createTrustStore(
      String filename, String password, Map<String, T> certs)
      throws GeneralSecurityException, IOException {
    KeyStore ks = KeyStore.getInstance("JKS");
    try (InputStream in = Files.newInputStream(Paths.get(filename))) {
      ks.load(in, password.toCharArray());
    } catch (EOFException e) {
      ks = createEmptyKeyStore();
    }
    for (Map.Entry<String, T> cert : certs.entrySet()) {
      ks.setCertificateEntry(cert.getKey(), cert.getValue());
    }
    try (OutputStream out = Files.newOutputStream(Paths.get(filename))) {
      ks.store(out, password.toCharArray());
    }
  }

  private static Properties createSslConfig(File keyStoreFile, String password,
      File trustStoreFile, String trustStorePassword) {
    Properties sslConfigs = new Properties();

    sslConfigs.setProperty(SSL_KEYSTORE, keyStoreFile.getPath());
    sslConfigs.setProperty(SSL_KEYSTORE_TYPE, "JKS");
    sslConfigs.setProperty(SSL_KEYSTORE_PASSWORD, password);
    sslConfigs.setProperty(SSL_TRUSTSTORE, trustStoreFile.getPath());
    sslConfigs.setProperty(SSL_TRUSTSTORE_PASSWORD, trustStorePassword);
    sslConfigs.setProperty(SSL_TRUSTSTORE_TYPE, "JKS");

    sslConfigs.setProperty(SSL_PROTOCOLS, "any");
    sslConfigs.setProperty(SSL_CIPHERS, "any");

    return sslConfigs;
  }

  public static Properties createClientConfig(String certAlias, String cn,
      CertificateBuilder certificateBuilder)
      throws IOException, GeneralSecurityException {
    return createSslConfig(true, certAlias, cn, certificateBuilder);
  }

  public static Properties createServerConfig(String certAlias, String cn,
      CertificateBuilder certificateBuilder)
      throws IOException, GeneralSecurityException {
    return createSslConfig(false, certAlias, cn, certificateBuilder);
  }

  public static Properties createSslConfig(boolean client, String certAlias, String cn,
      CertificateBuilder certBuilder)
      throws IOException, GeneralSecurityException {

    Map<String, X509Certificate> certs = new HashMap<>();
    File keyStoreFile;
    File trustStoreFile;

    String password = "Password";
    String trustStorePassword = "TrustStorePassword";

    if (client) {
      keyStoreFile = File.createTempFile("clientKS", ".jks");
      KeyPair cKP = generateKeyPair("RSA");
      X509Certificate cCert = certBuilder.generate("CN=" + cn + ", O=A client", cKP);
      createKeyStore(keyStoreFile.getPath(), password, "client", cKP.getPrivate(), cCert);
      certs.put(certAlias, cCert);
      keyStoreFile.deleteOnExit();
    } else {
      keyStoreFile = File.createTempFile("serverKS", ".jks");
      KeyPair sKP = generateKeyPair("RSA");
      X509Certificate sCert = certBuilder.generate("CN=" + cn + ", O=A server", sKP);
      createKeyStore(keyStoreFile.getPath(), password, "server", sKP.getPrivate(), sCert);
      certs.put(certAlias, sCert);
      keyStoreFile.deleteOnExit();
    }

    String trustStoreName = client ? "clientTS" : "serverTS";
    trustStoreFile = File.createTempFile(trustStoreName, ".jks");
    trustStoreFile.deleteOnExit();
    createTrustStore(trustStoreFile.getPath(), trustStorePassword, certs);

    return createSslConfig(keyStoreFile, password, trustStoreFile, trustStorePassword);
  }

  public static class CertificateBuilder {
    private final int days;
    private final String algorithm;
    private byte[] subjectAltName;

    public CertificateBuilder() {
      this(30, "SHA1withRSA");
    }

    public CertificateBuilder(int days, String algorithm) {
      this.days = days;
      this.algorithm = algorithm;
    }

    public CertificateBuilder sanDnsName(String hostName) throws IOException {
      subjectAltName =
          new GeneralNames(new GeneralName(GeneralName.dNSName, hostName)).getEncoded();
      return this;
    }

    public CertificateBuilder sanIpAddress(InetAddress hostAddress) throws IOException {
      subjectAltName = new GeneralNames(
          new GeneralName(GeneralName.iPAddress, new DEROctetString(hostAddress.getAddress())))
              .getEncoded();
      return this;
    }

    public X509Certificate generate(String dn, KeyPair keyPair) throws CertificateException {
      try {
        Security.addProvider(new BouncyCastleProvider());
        AlgorithmIdentifier sigAlgId =
            new DefaultSignatureAlgorithmIdentifierFinder().find(algorithm);
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
        AsymmetricKeyParameter privateKeyAsymKeyParam =
            PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
        SubjectPublicKeyInfo subPubKeyInfo =
            SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        ContentSigner sigGen =
            new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);
        X500Name name = new X500Name(dn);
        Date from = new Date();
        Date to = new Date(from.getTime() + days * 86400000L);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X509v3CertificateBuilder v3CertGen =
            new X509v3CertificateBuilder(name, sn, from, to, name, subPubKeyInfo);

        if (subjectAltName != null)
          v3CertGen.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
        X509CertificateHolder certificateHolder = v3CertGen.build(sigGen);
        return new JcaX509CertificateConverter().setProvider("BC")
            .getCertificate(certificateHolder);
      } catch (CertificateException ce) {
        throw ce;
      } catch (Exception e) {
        throw new CertificateException(e);
      }
    }
  }
}
