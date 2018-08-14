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
package org.apache.geode.cache.ssl;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.geode.distributed.ConfigurationProperties;

public class CertStores {

  public static final Set<String> TRUSTSTORE_PROPS = mkSet(
      ConfigurationProperties.SSL_TRUSTSTORE,
      ConfigurationProperties.SSL_TRUSTSTORE_TYPE,
      ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD);

  private final Properties sslConfig;

  public static <T> Set<T> mkSet(T... elems) {
    return new HashSet<>(Arrays.asList(elems));
  }

  public CertStores(boolean client, String commonName) throws Exception {
    this(client, commonName, new TestSSLUtils.CertificateBuilder());
  }

  public CertStores(boolean client, String commonName, String sanHostName) throws Exception {
    this(client, commonName, new TestSSLUtils.CertificateBuilder().sanDnsName(sanHostName));
  }

  public CertStores(boolean client, String commonName, InetAddress hostAddress) throws Exception {
    this(client, commonName, new TestSSLUtils.CertificateBuilder().sanIpAddress(hostAddress));
  }

  private CertStores(boolean client, String commonName, TestSSLUtils.CertificateBuilder certBuilder)
      throws Exception {
    sslConfig = TestSSLUtils.createSslConfig(client, commonName, certBuilder);
  }

  public Properties getSslConfig() {
    return sslConfig;
  }

  public Properties getTrustingConfig(CertStores truststoreConfig) {
    Properties config = new Properties();
    sslConfig.forEach((key, value) -> config.setProperty((String) key, (String) value));
    TRUSTSTORE_PROPS.forEach(
        propName -> config.setProperty(propName, truststoreConfig.sslConfig.getProperty(propName)));
    return config;
  }
}
