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
package org.apache.geode.management.cli;

import java.util.List;
import java.util.function.Function;

import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.management.internal.cli.result.ResultBuilder;

public class CommandExecutionContext {

  private Object functionArgs;
  private boolean executeOnSingleMember;
  private CliFunction cliFunction;
  private Function<List<CliFunctionResult>, CommandResult> resultConsumer;
  private ConfigurationMutator mutator;

  public CommandExecutionContext(Object functionArgs, boolean updateClusterConfig, CliFunction function, boolean executeOnSingleMember) {
    this(functionArgs,  function, executeOnSingleMember, DEFAULT_CONSUMER, null);
  }

  // for describe mapping/connection
  public CommandExecutionContext(Object functionArgs, CliFunction function) {
    this(functionArgs,  function, false, DEFAULT_CONSUMER, null);
  }

  public CommandExecutionContext(Object functionArgs, CliFunction function, ConfigurationMutator mutator) {
    this(functionArgs,  function, false, DEFAULT_CONSUMER, mutator);
  }
  public CommandExecutionContext(Object functionArgs, CliFunction function, boolean executeOnSingleMember, Function<List<CliFunctionResult>, CommandResult> resultConsumer, ConfigurationMutator mutator) {
    this.functionArgs = functionArgs;
    this.cliFunction = function;
    this.executeOnSingleMember = executeOnSingleMember;
    this.resultConsumer = resultConsumer;
    this.mutator = mutator;
  }

  public Function<List<CliFunctionResult>, CommandResult> getResultConsumer() {
    return resultConsumer;
  }

  public CliFunction getCliFunction() {
    return cliFunction;
  }


  public Object getFunctionArgs() {
    return functionArgs;
  }

  public ConfigurationMutator getMutator() {
    return mutator;
  }

  public boolean isExecuteOnSingleMember() {
    return executeOnSingleMember;
  }

  public static Function<List<CliFunctionResult>, CommandResult> DEFAULT_CONSUMER = (results) -> ResultBuilder.buildResult(results);

  public static Function<List<CliFunctionResult>, CommandResult> ALTER_CONSUMER = results -> {
    CommandResult result = ResultBuilder.buildResult(results);
    CliFunctionResult successResult =
        results.stream().filter(CliFunctionResult::isSuccessful).findAny().get();
    result.setConfigObject(successResult.getResultObject());
    return result;
  };
}
