package org.apache.geode.management.cli;

import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.ClusterCacheElement;
import org.apache.geode.management.internal.cli.result.model.ResultModel;

public interface ClusterConfigurationService {
  ResultModel persistCacheElement(CacheElement config, String group, String member,
      ClusterCacheElement.Operation operation, boolean ifExistsOrNotExists);
}
