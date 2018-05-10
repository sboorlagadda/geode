/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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
import org.apache.geode.connectors.jdbc.internal.RegionMappingNotFoundException;
import org.apache.geode.internal.cache.InternalCache;

public class ClusterRegionMapping extends ConnectorService.RegionMapping
    implements ClusterCacheElement {

  public ClusterRegionMapping() {};

  public ClusterRegionMapping(String regionName, String pdxClassName, String tableName,
      String connectionConfigName, Boolean primaryKeyInValue) {
    super(regionName, pdxClassName, tableName, connectionConfigName, primaryKeyInValue);
  }

  public void create(Cache cache) throws Exception {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);
    service.createRegionMapping(this);
  }

  public void deleteFrom(Cache cache) throws Exception {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);
    if (getExisting(cache) == null) {
      throw new RegionMappingNotFoundException(
          "RegionMapping for region '" + getRegionName() + "' does not exists.");
    }
    service.destroyRegionMapping(this.getRegionName());
  }

  public CacheElement getExisting(Cache cache) {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);
    return service.getMappingForRegion(getRegionName());
  }

  public void update(Cache cache) throws Exception {
    JdbcConnectorService service = ((InternalCache) cache).getService(JdbcConnectorService.class);

    ConnectorService.RegionMapping existingMapping =
        (ConnectorService.RegionMapping) getExisting(cache);
    if (existingMapping == null) {
      throw new RegionMappingNotFoundException(
          "RegionMapping for region '" + getRegionName() + "' does not exists.");
    }

    setConnectionConfigName(
        merge(getConnectionConfigName(), existingMapping.getConnectionConfigName()));
    setTableName(merge(getTableName(), existingMapping.getTableName()));
    setPdxClassName(merge(getPdxClassName(), existingMapping.getPdxClassName()));

    if (isPrimaryKeyInValue() == null) {
      setPrimaryKeyInValue(existingMapping.isPrimaryKeyInValue());
    }

    if (!isFieldMappingModified()) {
      getFieldMapping().addAll(existingMapping.getFieldMapping());
    }

    service.replaceRegionMapping(this);
  }

  public CacheElement getExisting(CacheConfig cacheConfig) {
    ConnectorService service =
        cacheConfig.findCustomCacheElement("connector-service", ConnectorService.class);
    if (service == null) {
      return null;
    }
    return CacheElement.findElement(service.getRegionMapping(), getId());
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

  public void update(CacheConfig config) {
    ConnectorService service =
        config.findCustomCacheElement("connector-service", ConnectorService.class);
    // service is not null at this point
    CacheElement.removeElement(service.getRegionMapping(), getId());
    service.getRegionMapping().add(this);
  }
}
