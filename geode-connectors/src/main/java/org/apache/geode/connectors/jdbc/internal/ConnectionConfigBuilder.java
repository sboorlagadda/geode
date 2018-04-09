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
package org.apache.geode.connectors.jdbc.internal;

import static org.apache.geode.connectors.jdbc.internal.xml.JdbcConnectorServiceXmlParser.PARAMS_DELIMITER;

import java.util.HashMap;
import java.util.Map;

import org.apache.geode.annotations.Experimental;
import org.apache.geode.connectors.jdbc.internal.configuration.ConnectorService;

@Experimental
public class ConnectionConfigBuilder {

  private String name;
  private String url;
  private String user;
  private String password;
  private Map<String, String> parameters;

  public ConnectionConfigBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ConnectionConfigBuilder withUrl(String url) {
    this.url = url;
    return this;
  }

  public ConnectionConfigBuilder withUser(String user) {
    this.user = user;
    return this;
  }

  public ConnectionConfigBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public ConnectionConfigBuilder withParameters(String[] params) {
    if (params != null) {
      parameters = new HashMap<>();
      for (String param : params) {
        if (param.isEmpty()) {
          continue;
        }
        String[] keyValuePair = param.split(PARAMS_DELIMITER);
        validateParam(keyValuePair, param);
        parameters.put(keyValuePair[0], keyValuePair[1]);
      }
    } else {
      parameters = null;
    }
    return this;
  }

  private void validateParam(String[] paramKeyValue, String param) {
    // paramKeyValue is produced by split which will never give us
    // an empty second element
    if ((paramKeyValue.length != 2) || paramKeyValue[0].isEmpty()) {
      throw new IllegalArgumentException("Parameter '" + param
          + "' is not of the form 'parameterName" + PARAMS_DELIMITER + "value'");
    }
  }

  public ConnectorService.Connection build() {
    return new ConnectorService.Connection(name, url, user, password, parameters);
  }
}
