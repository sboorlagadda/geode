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
package org.apache.geode.management.internal.cli.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import org.apache.geode.management.internal.cli.functions.ExportLogsFunction;
import org.apache.geode.management.internal.cli.functions.SizeExportLogsFunction;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.categories.LoggingTest;

@Category({GfshTest.class, LoggingTest.class})
public class LogSizerTest {

  private LogFilter logFilter;
  private SizeExportLogsFunction.Args nonFilteringArgs;

  @Rule
  public TestName testName = new TestName();

  private File mockStatFile;
  private File mockLogFile;

  @Before
  public void setUp() throws Exception {
    logFilter = mock(LogFilter.class);
    nonFilteringArgs = new ExportLogsFunction.Args(null, null, null, false, false, false);
    mockStatFile = mock(File.class);
    mockLogFile = mock(File.class);
    when(mockLogFile.toPath()).thenReturn(Paths.get("root", "parent", testName + ".log"));
    when(mockStatFile.toPath()).thenReturn(Paths.get("root", "parent", testName + ".gfs"));
  }

  @Test
  public void nullFileArgs_returnsZeroSize() throws Exception {
    LogExporter sizer = new LogExporter(logFilter, null, null);
    assertThat(sizer.estimateFilteredSize()).isEqualTo(0L);
  }

  @Test
  public void noFiles_returnsZeroSize() throws Exception {
    File mockStatFile = mock(File.class);
    File mockLogFile = mock(File.class);
    when(mockLogFile.toPath()).thenReturn(
       Paths.get("root", "parent", testName + ".log"));
    when(mockStatFile.toPath()).thenReturn(
       Paths.get("root", "parent", testName + ".gfs"));
    LogExporter sizer = new LogExporter(logFilter, mockLogFile, mockStatFile);
    assertThat(sizer.estimateFilteredSize()).isEqualTo(0L);
  }

  @Test
  public void emptyFiles_returnsZeroSize() throws Exception {
    File mockStatFile = mock(File.class);
    File mockLogFile = mock(File.class);
    when(mockLogFile.toPath()).thenReturn(
        Paths.get("root", "parent", testName + ".log"));
    when(mockStatFile.toPath()).thenReturn(
        Paths.get("root", "parent", testName + ".gfs"));
    LogFilter logFilter =
        new LogFilter(nonFilteringArgs.getLogLevel(), nonFilteringArgs.isThisLogLevelOnly(),
            nonFilteringArgs.getStartTime(), nonFilteringArgs.getEndTime());

    LogExporter sizer = new LogExporter(logFilter, mockLogFile, mockStatFile);
    assertThat(sizer.estimateFilteredSize()).isEqualTo(0L);
  }
}
