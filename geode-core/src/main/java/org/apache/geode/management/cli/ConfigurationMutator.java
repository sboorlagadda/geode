package org.apache.geode.management.cli;

import org.apache.geode.cache.configuration.CacheConfig;

public interface ConfigurationMutator {
  /**
   * implement this method for updating the cluster configuration of the group
   *
   * the implementation should update the passed in config object with appropriate changes
   * if for any reason config can't be updated. throw a RuntimeException stating the reason.
   *
   * @param group the group name of the cluster config, never null
   * @param config the configuration object, never null
   * @param configObject the return value of CommandResult.getConfigObject. CommandResult is the
   *        return
   *        value of your command method.
   *
   *        it should throw some RuntimeException if update failed.
   */
  void updateClusterConfig(String group, CacheConfig config, Object configObject);
}
