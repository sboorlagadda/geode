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

package org.apache.geode.cache.configuration;

public interface ClusterCacheElement extends CacheElement, ServerCacheElement, LocatorCacheElement {
  enum Operation {
    ADD, DELETE, UPDATE
  }

  default String merge(String newValue, String existingValue) {
    // if newValue is null use the value already in the config
    // if newValue is the empty string, then "unset" it by returning null
    if (newValue == null) {
      return existingValue;
    }
    return newValue.isEmpty() ? null : newValue;
  }
}
