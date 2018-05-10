package org.apache.geode.management.internal.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.ClusterCacheElement;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.management.cli.ClusterConfigurationService;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.exceptions.EntityExistsException;
import org.apache.geode.management.internal.cli.exceptions.EntityNotFoundException;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.functions.UpdateCacheFunction;
import org.apache.geode.management.internal.cli.result.model.InfoResultModel;
import org.apache.geode.management.internal.cli.result.model.ResultModel;

public class LocatorClusterConfigurationService implements ClusterConfigurationService {
  private static Logger logger = LogService.getLogger();
  private InternalCache cache;
  private ConfigurationPersistenceService persistenceService;

  public LocatorClusterConfigurationService(InternalCache cache,
      ConfigurationPersistenceService persistenceService) {
    this.cache = cache;
    this.persistenceService = persistenceService;
  }

  public ResultModel persistCacheElement(CacheElement element, String group, String member,
      ClusterCacheElement.Operation operation) {
    ClusterCacheElement config = (ClusterCacheElement) element;
    if (group != null && member != null) {
      throw new IllegalArgumentException("group and member can't be set at the same time.");
    }
    ResultModel result = new ResultModel();
    InfoResultModel info = result.addInfo();
    CacheConfig cacheConfig = null;
    // assuming config is validated at this point
    if (persistenceService != null && member == null) {
      cacheConfig = persistenceService.getCacheConfig(group, true);
      // see if the element already exists in this group's configuration
      boolean exists = config.getExisting(cacheConfig) != null;
      if (operation == ClusterCacheElement.Operation.ADD && exists) {
        throw new EntityExistsException("cache element " + config.getId() + " already exists.");
      } else if ((operation == ClusterCacheElement.Operation.UPDATE
          || operation == ClusterCacheElement.Operation.DELETE) && !exists) {
        throw new EntityNotFoundException("cache element " + config.getId() + " does not exists.");
      }
    }

    // execute function on all members
    final String[] groups = (group != null) ? new String[] {group} : null;
    final String[] members = (member != null) ? new String[] {member} : null;

    Set<DistributedMember> targetedMembers = findMembers(groups, members);
    if (targetedMembers.size() == 0) {
      info.addLine("No members found");
    } else {
      List<CliFunctionResult> functionResults = executeAndGetFunctionResult(
          new UpdateCacheFunction(), Arrays.asList(config, operation), targetedMembers);
      result.addTable(functionResults, null, null);
    }

    if (result.getStatus() == Result.Status.ERROR) {
      return result;
    }

    if (persistenceService == null) {
      info.addLine(
          "Cluster configuration service is not available. Configuration change is not persisted.");
    }

    if (member != null) {
      info.addLine("Operation is on a specific member. Configuration change is not persisted. ");
      return result;
    }

    // persist configuration
    final String groupName = (group == null) ? "cluster" : group;
    persistenceService.updateCacheConfig(groupName, c -> {
      try {
        switch (operation) {
          case ADD:
            config.add(c);
            break;
          case DELETE:
            config.deleteFrom(c);
            break;
          case UPDATE:
            config.update(c);
            break;
        }
      } catch (Exception e) {
        String message = "failed to update cluster config for " + groupName;
        logger.error(message, e);
        info.addLine(message + ". Reason: " + e.getMessage());
        return null;
      }
      return c;
    });
    return result;
  }

  private Set<DistributedMember> findMembers(String[] groups, String[] members) {
    return CliUtil.findMembers(groups, members, cache);
  }

  private List<CliFunctionResult> executeAndGetFunctionResult(Function function, Object args,
      Set<DistributedMember> targetMembers) {
    ResultCollector rc = CliUtil.executeFunction(function, args, targetMembers);
    return CliFunctionResult.cleanResults((List<?>) rc.getResult());
  }
}
