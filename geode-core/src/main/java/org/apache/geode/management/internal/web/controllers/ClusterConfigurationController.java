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
package org.apache.geode.management.internal.web.controllers;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.apache.geode.distributed.internal.InternalLocator.getLocator;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.geode.cache.configuration.ClusterCacheElement;
import org.apache.geode.distributed.internal.InternalLocator;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.result.model.ResultModel;

@Controller("clusterconfiguration")
@RequestMapping(AbstractCommandsController.REST_API_VERSION)
public class ClusterConfigurationController extends AbstractCommandsController {

  private InternalLocator locator;

  @RequestMapping(method = {RequestMethod.GET}, value = "/cache/configuration")
  public ResponseEntity<InputStreamResource> get() {
    return getJsonResponse(ResultModel.createInfo("success"));
  }

  @RequestMapping(method = {RequestMethod.POST}, value = "/cache/configuration")
  public ResponseEntity<InputStreamResource> post(@RequestBody ClusterCacheElement cacheElement,
      @RequestParam(value = "group", required = false) String group,
      @RequestParam(value = "member", required = false) String member,
      @RequestParam(value = "ifExistsOrNotExists", required = false) Boolean ifExistsOrNotExists) {

    ResultModel
        result = getLocator().getClusterConfigurationService().persistCacheElement(cacheElement, group, member, ClusterCacheElement.Operation.ADD, ifExistsOrNotExists);
    return getJsonResponse(result);
  }

  @RequestMapping(method = {RequestMethod.PUT}, value = "/cache/configuration")
  public ResponseEntity<InputStreamResource> put(@RequestBody ClusterCacheElement cacheElement,
      @RequestParam(value = "group", required = false) String group,
      @RequestParam(value = "member", required = false) String member,
      @RequestParam(value = "ifExistsOrNotExists", required = false) Boolean ifExistsOrNotExists) {

    ResultModel
        result = getLocator().getClusterConfigurationService().persistCacheElement(cacheElement, group, member, ClusterCacheElement.Operation.UPDATE, ifExistsOrNotExists);
    return getJsonResponse(result);
  }

  @RequestMapping(method = {RequestMethod.DELETE}, value = "/cache/configuration")
  public ResponseEntity<InputStreamResource> delete(@RequestBody ClusterCacheElement cacheElement,
      @RequestParam(value = "group", required = false) String group,
      @RequestParam(value = "member", required = false) String member,
      @RequestParam(value = "ifExistsOrNotExists", required = false) Boolean ifExistsOrNotExists) {
    ResultModel
        result = getLocator().getClusterConfigurationService().persistCacheElement(cacheElement, group, member, ClusterCacheElement.Operation.DELETE, ifExistsOrNotExists);
    return getJsonResponse(result);
  }

  private ResponseEntity<InputStreamResource> getJsonResponse(ResultModel resultModel) {
    HttpHeaders respHeaders = new HttpHeaders();
    try {
      InputStreamResource isr = new InputStreamResource(toInputStream(resultModel.toJson(), "UTF-8"));
      respHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      if(resultModel.getStatus() == Result.Status.OK) {
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      throw new RuntimeException("IO Error writing file to output stream", e);
    }
  }
}
