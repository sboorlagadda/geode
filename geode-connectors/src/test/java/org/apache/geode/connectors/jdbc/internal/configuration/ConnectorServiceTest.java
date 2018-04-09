/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.connectors.jdbc.internal.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.internal.ClassPathLoader;
import org.apache.geode.internal.config.JAXBService;
import org.apache.geode.test.junit.categories.UnitTest;


@Category(UnitTest.class)
public class ConnectorServiceTest {

  private JAXBService jaxbService;
  @Before
  public void setUp() throws Exception {
    jaxbService = new JAXBService();
    jaxbService.registerBindClassWithSchema(ConnectorService.class, ConnectorService.SCHEMA);
    // find the local jdbc-1.0.xsd
    URL local_xsd = ClassPathLoader.getLatest()
        .getResource("META-INF/schemas/geode.apache.org/schema/jdbc/jdbc-1.0.xsd");
    jaxbService.validateWith(local_xsd);
  }

  @Test
  public void connectorServiceCanBeCorrectlyMarshalled() {
    ConnectorService service = new ConnectorService();
    ConnectorService.Connection connection = new ConnectorService.Connection("name", "url", "user", "password", (String[])null);
    connection.setParameters("key:value,key1:value1");
    service.getConnection().add(connection);

    assertThat(connection.getParameterMap()).hasSize(2);
    assertThat(connection.getParameterMap()).containsOnlyKeys("key", "key1");
    assertThat(connection.getParameterMap()).containsValues("value", "value1");

    String xml = jaxbService.marshall(service);
    System.out.println(xml);

    assertThat(xml).contains("name=\"name\"")
        .contains("url=\"url\"")
        .contains("user=\"user\"")
        .contains("password=\"password\"")
        .contains("parameters=\"key:value,key1:value1\"");

    assertThat(xml).contains("xmlns:jdbc=\"http://geode.apache.org/schema/jdbc\"");
    assertThat(xml).contains("<jdbc:connection");
  }
}