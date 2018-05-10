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

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.ClusterCacheElement;
import org.apache.geode.connectors.jdbc.internal.JdbcConnectorService;
import org.apache.geode.internal.cache.InternalCache;

public class ClusterRegionMapping extends ConnectorService.RegionMapping implements
    ClusterCacheElement {

  public ClusterRegionMapping() {};
  public ClusterRegionMapping(String regionName, String pdxClassName, String tableName,
                       String connectionConfigName, Boolean primaryKeyInValue){
    super(regionName, pdxClassName, tableName, connectionConfigName, primaryKeyInValue);
  }
  public void create(Cache cache) throws Exception {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);
    service.createRegionMapping(this);
  }

  public void deleteFrom(Cache cache) throws Exception {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);
    ConnectorService.RegionMapping mapping = service.getMappingForRegion(regionName);
    if (mapping != null) {
      service.destroyRegionMapping(regionName);
    }
  }

  @Override
  public boolean exists(Cache cache) {
    return false;
  }

  @Override
  public void update(Cache cache) throws Exception {

  }

  public boolean exist(CacheConfig cacheConfig) {
    ConnectorService service =
        cacheConfig.findCustomCacheElement("connector-service", ConnectorService.class);
    if (service == null) {
      return false;
    }
    return CacheElement.findElement(service.getRegionMapping(), getId()) != null;
  }

  public void add(CacheConfig config) {
    ConnectorService service =
        config.findCustomCacheElement("connector-service", ConnectorService.class);
    if (service == null) {
      service = new ConnectorService();
      config.getCustomCacheElements().add(service);
    }
    service.getRegionMapping().add(this);
  }

  public void deleteFrom(CacheConfig config) {
    ConnectorService service =
        config.findCustomCacheElement("connector-service", ConnectorService.class);
    if (service != null) {
      CacheElement.removeElement(service.getRegionMapping(), this.getId());
    }
  }

  @Override
  public void update(CacheConfig cache) {

  }
}
