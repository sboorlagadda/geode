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

public class CommandContext {

  private Object functionArgs;
  private boolean updateClusterConfig;
  private boolean executeOnSingleMember;
  private CliFunction cliFunction;

  public CommandContext(Object functionArgs, boolean updateClusterConfig, CliFunction function, boolean executeOnSingleMember) {
    this.functionArgs = functionArgs;
    this.updateClusterConfig = updateClusterConfig;
    this.cliFunction = function;
    this.executeOnSingleMember = executeOnSingleMember;
  }

  public CliFunction getCliFunction() {
    return cliFunction;
  }

  public void setCliFunction(CliFunction cliFunction) {
    this.cliFunction = cliFunction;
  }

  public Object getFunctionArgs() {
    return functionArgs;
  }

  public void setFunctionArgs(Object functionArgs) {
    this.functionArgs = functionArgs;
  }

  public boolean isUpdateClusterConfig() {
    return updateClusterConfig;
  }

  public void setUpdateClusterConfig(boolean updateClusterConfig) {
    this.updateClusterConfig = updateClusterConfig;
  }

  public boolean isExecuteOnSingleMember() {
    return executeOnSingleMember;
  }

  public void setExecuteOnSingleMember(boolean executeOnSingleMember) {
    this.executeOnSingleMember = executeOnSingleMember;
  }
}
