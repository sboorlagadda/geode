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
 *
 */
package org.apache.geode.tools.pulse.ui;

import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_CLIENTS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_FUNCTIONS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_GCPAUSES_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_QUERIESPERSEC_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_READPERSEC_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_SUBSCRIPTION_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_UNIQUECQS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_VIEW_LOCATORS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_VIEW_MEMBERS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_VIEW_REGIONS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.CLUSTER_WRITEPERSEC_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_CPUUSAGE_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_JVMPAUSES_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_LOADAVG_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_READPERSEC_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_SOCKETS_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_THREAD_ID;
import static org.apache.geode.tools.pulse.ui.PulseTestConstants.MEMBER_VIEW_WRITEPERSEC_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.text.DecimalFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.apache.geode.tools.pulse.internal.data.Cluster;

public abstract class PulseBase {
  public abstract WebDriver getWebDriver();

  public abstract String getPulseURL();

  public abstract Cluster getCluster();

  private DecimalFormat df = new DecimalFormat("0.00");

  @Before
  public void setup() {
    // Make sure we go to the home page first
    searchByXPathAndClick("//a[text()='Cluster View']");
  }

  protected void searchByLinkAndClick(String linkText) {
    WebElement element = By.linkText(linkText).findElement(getWebDriver());
    assertNotNull(element);
    element.click();
  }

  protected void searchByIdAndClick(String id) {
    WebElement element = getWebDriver().findElement(By.id(id));
    assertNotNull(element);
    element.click();
  }

  protected void searchByXPathAndClick(String xpath) {
    WebElement element = getWebDriver().findElement(By.xpath(xpath));
    assertNotNull(element);
    element.click();
  }

  @Test
  public void testClusterDetailsAndWidgets() {
    String displayedLocatorCount =
        getWebDriver().findElement(By.id(CLUSTER_VIEW_LOCATORS_ID)).getText();
    assertThat(String.valueOf(getCluster().getLocatorCount())).isEqualTo(displayedLocatorCount);

    String displayedRegionCount =
        getWebDriver().findElement(By.id(CLUSTER_VIEW_REGIONS_ID)).getText();
    assertThat(String.valueOf(getCluster().getTotalRegionCount())).isEqualTo(displayedRegionCount);

    String displayedClusterMemberCount =
        getWebDriver().findElement(By.id(CLUSTER_VIEW_MEMBERS_ID)).getText();
    assertThat(String.valueOf(getCluster().getMemberCount()))
        .isEqualTo(displayedClusterMemberCount);

    String displyedClusterClients = getWebDriver().findElement(By.id(CLUSTER_CLIENTS_ID)).getText();
    assertThat(String.valueOf(getCluster().getClientConnectionCount()))
        .isEqualTo(displyedClusterClients);

    String displayedFnRunningCount =
        getWebDriver().findElement(By.id(CLUSTER_FUNCTIONS_ID)).getText();
    assertThat(String.valueOf(getCluster().getRunningFunctionCount()))
        .isEqualTo(displayedFnRunningCount);

    String displayedUniqueCQs = getWebDriver().findElement(By.id(CLUSTER_UNIQUECQS_ID)).getText();
    assertThat(String.valueOf(getCluster().getRegisteredCQCount())).isEqualTo(displayedUniqueCQs);

    String displayedSubscriptionCount =
        getWebDriver().findElement(By.id(CLUSTER_SUBSCRIPTION_ID)).getText();
    assertThat(String.valueOf(getCluster().getSubscriptionCount()))
        .isEqualTo(displayedSubscriptionCount);

    // TODO: verify it is the right counter
    String displayedGCPauses = getWebDriver().findElement(By.id(CLUSTER_GCPAUSES_ID)).getText();
    assertThat(String.valueOf(getCluster().getPreviousJVMPauseCount()))
        .isEqualTo(displayedGCPauses);

    String clusterWritePerSec = getWebDriver().findElement(By.id(CLUSTER_WRITEPERSEC_ID)).getText();
    assertThat(df.format(getCluster().getWritePerSec())).isEqualTo(clusterWritePerSec);

    String clusterReadPerSec = getWebDriver().findElement(By.id(CLUSTER_READPERSEC_ID)).getText();
    assertThat(df.format(getCluster().getReadPerSec())).isEqualTo(clusterReadPerSec);

    String clusterQueriesPerSec =
        getWebDriver().findElement(By.id(CLUSTER_QUERIESPERSEC_ID)).getText();
    assertThat(df.format(getCluster().getQueriesPerSec())).isEqualTo(clusterQueriesPerSec);
  }

  @Test
  public void testClusterGridViewMemberDetails() throws InterruptedException {
    // TODO: see how to verify heap & cpu as they can change while asserting

    searchByIdAndClick("default_grid_button");
    List<WebElement> elements =
        getWebDriver().findElements(By.xpath("//table[@id='memberList']/tbody/tr"));

    Cluster.Member[] actualMembers = getCluster().getMembers();
    // table contains header row so actual members is one less than the tr elements.
    assertThat(actualMembers.length).isEqualTo(elements.size() - 1);

    for (int i = 0; i < actualMembers.length; i++) {
      String displayedMemberId =
          getWebDriver().findElement(By.xpath("//table[@id='memberList']/tbody/tr[contains(@id, '"
              + actualMembers[i].getName() + "')]/td")).getText();
      assertThat(actualMembers[i].getId()).contains(displayedMemberId);

      String displayedMemberName =
          getWebDriver().findElement(By.xpath("//table[@id='memberList']/tbody/tr[contains(@id, '"
              + actualMembers[i].getName() + "')]/td[2]")).getText();
      assertThat(actualMembers[i].getName()).isEqualTo(displayedMemberName);

      String displayedMemberHost =
          getWebDriver().findElement(By.xpath("//table[@id='memberList']/tbody/tr[contains(@id, '"
              + actualMembers[i].getName() + "')]/td[3]")).getText();
      assertThat(actualMembers[i].getHost()).isEqualTo(displayedMemberHost);

      String displayedHeapUsage =
          getWebDriver().findElement(By.xpath("//table[@id='memberList']/tbody/tr[contains(@id, '"
              + actualMembers[i].getName() + "')]/td[5]")).getText();
      // assertThat(String.valueOf(actualMembers[i].getCurrentHeapSize())).isEqualTo(displayedHeapUsage);

      String displayedCPUUsage =
          getWebDriver().findElement(By.xpath("//table[@id='memberList']/tbody/tr[contains(@id, '"
              + actualMembers[i].getName() + "')]/td[6]")).getText();
      // assertThat(df.format(actualMembers[i].getCpuUsage())).isEqualTo(displayedCPUUsage);

      // now click the grid row to go to member view and assert details displayed
      searchByXPathAndClick("//table[@id='memberList']/tbody/tr[contains(@id, '"
          + actualMembers[i].getName() + "')]/td");

      String displayedThreadsCount =
          getWebDriver().findElement(By.id(MEMBER_VIEW_THREAD_ID)).getText();
      // assertThat(String.valueOf(actualMembers[i].getNumThreads())).isEqualTo(displayedThreadsCount);

      String displaySocketCount =
          getWebDriver().findElement(By.id(MEMBER_VIEW_SOCKETS_ID)).getText();
      if (actualMembers[i].getTotalFileDescriptorOpen() < 0)
        assertThat("NA").isEqualTo(displaySocketCount);
      else
        assertThat(String.valueOf(actualMembers[i].getTotalFileDescriptorOpen()))
            .isEqualTo(displaySocketCount);

      String displayedLoadAverage =
          getWebDriver().findElement(By.id(MEMBER_VIEW_LOADAVG_ID)).getText();
      // assertThat(df.format(actualMembers[i].getLoadAverage())).isEqualTo(displayedLoadAverage);

      String displayedJVMPauses =
          getWebDriver().findElement(By.id(MEMBER_VIEW_JVMPAUSES_ID)).getText();
      assertThat(String.valueOf(actualMembers[i].getPreviousJVMPauseCount()))
          .isEqualTo(displayedJVMPauses);

      String displayedMemberCPUUsage =
          getWebDriver().findElement(By.id(MEMBER_VIEW_CPUUSAGE_ID)).getText();
      // assertThat(df.format(actualMembers[i].getCpuUsage())).isEqualTo(displayedMemberCPUUsage);

      String displayedReadsPerSecond =
          getWebDriver().findElement(By.id(MEMBER_VIEW_READPERSEC_ID)).getText();
      // assertThat(df.format(actualMembers[i].getThroughputReads())).isEqualTo(displayedReadsPerSecond);

      String displayedWritePerSec =
          getWebDriver().findElement(By.id(MEMBER_VIEW_WRITEPERSEC_ID)).getText();
      assertThat(df.format(actualMembers[i].getThroughputWrites())).isEqualTo(displayedWritePerSec);
    }
  }

  @Test
  public void userCanGetToPulseDetails() {
    getWebDriver().get(getPulseURL() + "pulseVersion");
    assertThat(getWebDriver().getPageSource()).contains("sourceRevision");
  }

  // public void testRgraphWidget() throws InterruptedException {
  // searchByIdAndClick("default_rgraph_button");
  // searchByIdAndClick("h1");
  // searchByIdAndClick("M1");
  // }
  //
  // @Test
  // @Ignore("ElementNotVisible with phantomJS")
  // public void testMemberTotalRegionCount() throws InterruptedException {
  // testRgraphWidget();
  // String RegionCount = getWebDriver().findElement(By.id(MEMBER_VIEW_REGION_ID)).getText();
  // String memberRegionCount =
  // JMXProperties.getInstance().getProperty("member.M1.totalRegionCount");
  // assertEquals(memberRegionCount, RegionCount);
  // }
  //
  //
  // @Ignore("WIP") // May be useful in near future
  // @Test
  // public void testOffHeapFreeSize() {
  //
  // String OffHeapFreeSizeString =
  // getWebDriver().findElement(By.id(MEMBER_VIEW_OFFHEAPFREESIZE_ID)).getText();
  // String OffHeapFreeSizetemp = OffHeapFreeSizeString.replaceAll("[a-zA-Z]", "");
  // float OffHeapFreeSize = Float.parseFloat(OffHeapFreeSizetemp);
  // float memberOffHeapFreeSize =
  // Float.parseFloat(JMXProperties.getInstance().getProperty("member.M1.OffHeapFreeSize"));
  // if (memberOffHeapFreeSize < 1048576) {
  // memberOffHeapFreeSize = memberOffHeapFreeSize / 1024;
  //
  // } else if (memberOffHeapFreeSize < 1073741824) {
  // memberOffHeapFreeSize = memberOffHeapFreeSize / 1024 / 1024;
  // } else {
  // memberOffHeapFreeSize = memberOffHeapFreeSize / 1024 / 1024 / 1024;
  // }
  // memberOffHeapFreeSize =
  // Float.parseFloat(new DecimalFormat("##.##").format(memberOffHeapFreeSize));
  // assertEquals(memberOffHeapFreeSize, OffHeapFreeSize);
  //
  // }
  //
  // @Ignore("WIP") // May be useful in near future
  // @Test
  // public void testOffHeapUsedSize() throws InterruptedException {
  //
  // String OffHeapUsedSizeString =
  // getWebDriver().findElement(By.id(MEMBER_VIEW_OFFHEAPUSEDSIZE_ID)).getText();
  // String OffHeapUsedSizetemp = OffHeapUsedSizeString.replaceAll("[a-zA-Z]", "");
  // float OffHeapUsedSize = Float.parseFloat(OffHeapUsedSizetemp);
  // float memberOffHeapUsedSize =
  // Float.parseFloat(JMXProperties.getInstance().getProperty("member.M1.OffHeapUsedSize"));
  // if (memberOffHeapUsedSize < 1048576) {
  // memberOffHeapUsedSize = memberOffHeapUsedSize / 1024;
  //
  // } else if (memberOffHeapUsedSize < 1073741824) {
  // memberOffHeapUsedSize = memberOffHeapUsedSize / 1024 / 1024;
  // } else {
  // memberOffHeapUsedSize = memberOffHeapUsedSize / 1024 / 1024 / 1024;
  // }
  // memberOffHeapUsedSize =
  // Float.parseFloat(new DecimalFormat("##.##").format(memberOffHeapUsedSize));
  // assertEquals(memberOffHeapUsedSize, OffHeapUsedSize);
  // }
  //
  // @Test
  // @Ignore("ElementNotVisible with phantomJS")
  // public void testMemberGridViewData() throws InterruptedException {
  // testRgraphWidget();
  // searchByXPathAndClick(PulseTestLocators.MemberDetailsView.gridButtonXpath);
  // // get the number of rows on the grid
  // List<WebElement> noOfRows =
  // getWebDriver().findElements(By.xpath("//table[@id='memberRegionsList']/tbody/tr"));
  // String MemberRegionName = getWebDriver()
  // .findElement(By.xpath("//table[@id='memberRegionsList']/tbody/tr[2]/td[1]")).getText();
  // String memberRegionName = JMXProperties.getInstance().getProperty("region.R1.name");
  // assertEquals(memberRegionName, MemberRegionName);
  //
  // String MemberRegionType = getWebDriver()
  // .findElement(By.xpath("//table[@id='memberRegionsList']/tbody/tr[2]/td[2]")).getText();
  // String memberRegionType = JMXProperties.getInstance().getProperty("region.R1.regionType");
  // assertEquals(memberRegionType, MemberRegionType);
  //
  // String MemberRegionEntryCount = getWebDriver()
  // .findElement(By.xpath("//table[@id='memberRegionsList']/tbody/tr[2]/td[3]")).getText();
  // String memberRegionEntryCount =
  // JMXProperties.getInstance().getProperty("regionOnMember./R1.M1.entryCount");
  // assertEquals(memberRegionEntryCount, MemberRegionEntryCount);
  // }
  //
  // @Test
  // public void testDropDownList() throws InterruptedException {
  // searchByIdAndClick("default_grid_button");
  // searchByIdAndClick("M1&M1");
  // searchByIdAndClick("memberName");
  // searchByLinkAndClick("M3");
  // searchByIdAndClick("memberName");
  // searchByLinkAndClick("M2");
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewRegionName() throws InterruptedException {
  // searchByLinkAndClick(DATA_VIEW_LABEL);
  // Thread.sleep(7000);
  // searchByIdAndClick("default_grid_button");
  // String regionName = getWebDriver().findElement(By.id(REGION_NAME_LABEL)).getText();
  // String dataviewregionname = JMXProperties.getInstance().getProperty("region.R1.name");
  // assertEquals(dataviewregionname, regionName);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewRegionPath() {
  // String regionPath = getWebDriver().findElement(By.id(REGION_PATH_LABEL)).getText();
  // String dataviewregionpath = JMXProperties.getInstance().getProperty("region.R1.fullPath");
  // assertEquals(dataviewregionpath, regionPath);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewRegionType() {
  // String regionType = getWebDriver().findElement(By.id(REGION_TYPE_LABEL)).getText();
  // String dataviewregiontype = JMXProperties.getInstance().getProperty("region.R1.regionType");
  // assertEquals(dataviewregiontype, regionType);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewEmptyNodes() {
  // String regionEmptyNodes = getWebDriver().findElement(By.id(DATA_VIEW_EMPTYNODES)).getText();
  // String dataviewEmptyNodes = JMXProperties.getInstance().getProperty("region.R1.emptyNodes");
  // assertEquals(dataviewEmptyNodes, regionEmptyNodes);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewSystemRegionEntryCount() {
  // String regionEntryCount = getWebDriver().findElement(By.id(DATA_VIEW_ENTRYCOUNT)).getText();
  // String dataviewEntryCount =
  // JMXProperties.getInstance().getProperty("region.R1.systemRegionEntryCount");
  // assertEquals(dataviewEntryCount, regionEntryCount);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewPersistentEnabled() {
  // String regionPersistence =
  // getWebDriver().findElement(By.id(REGION_PERSISTENCE_LABEL)).getText();
  // String dataviewregionpersistence =
  // JMXProperties.getInstance().getProperty("region.R1.persistentEnabled");
  // assertEquals(dataviewregionpersistence, regionPersistence);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewDiskWritesRate() {
  // String regionWrites = getWebDriver().findElement(By.id(DATA_VIEW_WRITEPERSEC)).getText();
  // String dataviewRegionWrites =
  // JMXProperties.getInstance().getProperty("region.R1.diskWritesRate");
  // assertEquals(dataviewRegionWrites, regionWrites);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewDiskReadsRate() {
  // String regionReads = getWebDriver().findElement(By.id(DATA_VIEW_READPERSEC)).getText();
  // String dataviewRegionReads =
  // JMXProperties.getInstance().getProperty("region.R1.diskReadsRate");
  // assertEquals(dataviewRegionReads, regionReads);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewDiskUsage() {
  // String regionMemoryUsed = getWebDriver().findElement(By.id(DATA_VIEW_USEDMEMORY)).getText();
  // String dataviewMemoryUsed = JMXProperties.getInstance().getProperty("region.R1.diskUsage");
  // assertEquals(dataviewMemoryUsed, regionMemoryUsed);
  // searchByLinkAndClick(QUERY_STATISTICS_LABEL);
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testDataViewGridValue() {
  // String DataViewRegionName =
  // getWebDriver().findElement(By.xpath("//*[id('6')/x:td[1]]")).getText();
  // String dataViewRegionName = JMXProperties.getInstance().getProperty("region.R1.name");
  // assertEquals(dataViewRegionName, DataViewRegionName);
  //
  // String DataViewRegionType =
  // getWebDriver().findElement(By.xpath("//*[id('6')/x:td[2]")).getText();
  // String dataViewRegionType = JMXProperties.getInstance().getProperty("region.R2.regionType");
  // assertEquals(dataViewRegionType, DataViewRegionType);
  //
  // String DataViewEntryCount =
  // getWebDriver().findElement(By.xpath("//*[id('6')/x:td[3]")).getText();
  // String dataViewEntryCount =
  // JMXProperties.getInstance().getProperty("region.R2.systemRegionEntryCount");
  // assertEquals(dataViewEntryCount, DataViewEntryCount);
  //
  // String DataViewEntrySize =
  // getWebDriver().findElement(By.xpath("//*[id('6')/x:td[4]")).getText();
  // String dataViewEntrySize = JMXProperties.getInstance().getProperty("region.R2.entrySize");
  // assertEquals(dataViewEntrySize, DataViewEntrySize);
  //
  // }
  //
  //
  // public void loadDataBrowserpage() {
  // searchByLinkAndClick(DATA_BROWSER_LABEL);
  // // Thread.sleep(7000);
  // }
  //
  // @Test
  // public void testDataBrowserRegionName() throws InterruptedException {
  // loadDataBrowserpage();
  // String DataBrowserRegionName1 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_REGIONName1)).getText();
  // String databrowserRegionNametemp1 = JMXProperties.getInstance().getProperty("region.R1.name");
  // String databrowserRegionName1 = databrowserRegionNametemp1.replaceAll("[\\/]", "");
  // assertEquals(databrowserRegionName1, DataBrowserRegionName1);
  //
  // String DataBrowserRegionName2 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_REGIONName2)).getText();
  // String databrowserRegionNametemp2 = JMXProperties.getInstance().getProperty("region.R2.name");
  // String databrowserRegionName2 = databrowserRegionNametemp2.replaceAll("[\\/]", "");
  // assertEquals(databrowserRegionName2, DataBrowserRegionName2);
  //
  // String DataBrowserRegionName3 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_REGIONName3)).getText();
  // String databrowserRegionNametemp3 = JMXProperties.getInstance().getProperty("region.R3.name");
  // String databrowserRegionName3 = databrowserRegionNametemp3.replaceAll("[\\/]", "");
  // assertEquals(databrowserRegionName3, DataBrowserRegionName3);
  //
  // }
  //
  // @Test
  // public void testDataBrowserRegionMembersVerificaition() throws InterruptedException {
  // loadDataBrowserpage();
  // searchByIdAndClick(DATA_BROWSER_REGION1_CHECKBOX);
  // String DataBrowserMember1Name1 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member0']")).getText();
  // String DataBrowserMember1Name2 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member1']")).getText();
  // String DataBrowserMember1Name3 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member2']")).getText();
  // String databrowserMember1Names = JMXProperties.getInstance().getProperty("region.R1.members");
  //
  // String databrowserMember1Names1 = databrowserMember1Names.substring(0, 2);
  // assertEquals(databrowserMember1Names1, DataBrowserMember1Name1);
  //
  // String databrowserMember1Names2 = databrowserMember1Names.substring(3, 5);
  // assertEquals(databrowserMember1Names2, DataBrowserMember1Name2);
  //
  // String databrowserMember1Names3 = databrowserMember1Names.substring(6, 8);
  // assertEquals(databrowserMember1Names3, DataBrowserMember1Name3);
  // searchByIdAndClick(DATA_BROWSER_REGION1_CHECKBOX);
  //
  // searchByIdAndClick(DATA_BROWSER_REGION2_CHECKBOX);
  // String DataBrowserMember2Name1 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member0']")).getText();
  // String DataBrowserMember2Name2 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member1']")).getText();
  // String databrowserMember2Names = JMXProperties.getInstance().getProperty("region.R2.members");
  //
  // String databrowserMember2Names1 = databrowserMember2Names.substring(0, 2);
  // assertEquals(databrowserMember2Names1, DataBrowserMember2Name1);
  //
  // String databrowserMember2Names2 = databrowserMember2Names.substring(3, 5);
  // assertEquals(databrowserMember2Names2, DataBrowserMember2Name2);
  // searchByIdAndClick(DATA_BROWSER_REGION2_CHECKBOX);
  //
  // searchByIdAndClick(DATA_BROWSER_REGION3_CHECKBOX);
  // String DataBrowserMember3Name1 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member0']")).getText();
  // String DataBrowserMember3Name2 =
  // getWebDriver().findElement(By.xpath("//label[@for='Member1']")).getText();
  // String databrowserMember3Names = JMXProperties.getInstance().getProperty("region.R3.members");
  //
  // String databrowserMember3Names1 = databrowserMember3Names.substring(0, 2);
  // assertEquals(databrowserMember3Names1, DataBrowserMember3Name1);
  //
  // String databrowserMember3Names2 = databrowserMember3Names.substring(3, 5);
  // assertEquals(databrowserMember3Names2, DataBrowserMember3Name2);
  // searchByIdAndClick(DATA_BROWSER_REGION3_CHECKBOX);
  // }
  //
  // @Test
  // public void testDataBrowserColocatedRegions() throws InterruptedException {
  // loadDataBrowserpage();
  // String databrowserMemberNames1 = JMXProperties.getInstance().getProperty("region.R1.members");
  // String databrowserMemberNames2 = JMXProperties.getInstance().getProperty("region.R2.members");
  // String databrowserMemberNames3 = JMXProperties.getInstance().getProperty("region.R3.members");
  //
  // if ((databrowserMemberNames1.matches(databrowserMemberNames2 + "(.*)"))) {
  // if ((databrowserMemberNames1.matches(databrowserMemberNames3 + "(.*)"))) {
  // if ((databrowserMemberNames2.matches(databrowserMemberNames3 + "(.*)"))) {
  // System.out.println("R1, R2 and R3 are colocated regions");
  // }
  // }
  // }
  // searchByIdAndClick(DATA_BROWSER_REGION1_CHECKBOX);
  // searchByLinkAndClick(DATA_BROWSER_COLOCATED_REGION);
  // String DataBrowserColocatedRegion1 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_COLOCATED_REGION_NAME1)).getText();
  // String DataBrowserColocatedRegion2 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_COLOCATED_REGION_NAME2)).getText();
  // String DataBrowserColocatedRegion3 =
  // getWebDriver().findElement(By.id(DATA_BROWSER_COLOCATED_REGION_NAME3)).getText();
  //
  // String databrowserColocatedRegiontemp1 =
  // JMXProperties.getInstance().getProperty("region.R1.name");
  // String databrowserColocatedRegion1 = databrowserColocatedRegiontemp1.replaceAll("[\\/]", "");
  //
  // String databrowserColocatedRegiontemp2 =
  // JMXProperties.getInstance().getProperty("region.R2.name");
  // String databrowserColocatedRegion2 = databrowserColocatedRegiontemp2.replaceAll("[\\/]", "");
  //
  // String databrowserColocatedRegiontemp3 =
  // JMXProperties.getInstance().getProperty("region.R3.name");
  // String databrowserColocatedRegion3 = databrowserColocatedRegiontemp3.replaceAll("[\\/]", "");
  //
  // assertEquals(databrowserColocatedRegion1, DataBrowserColocatedRegion1);
  // assertEquals(databrowserColocatedRegion2, DataBrowserColocatedRegion2);
  // assertEquals(databrowserColocatedRegion3, DataBrowserColocatedRegion3);
  //
  // }
  //
  // @Ignore("WIP") // clusterDetails element not found on Data Browser page. No assertions in test
  // @Test
  // public void testDataBrowserQueryValidation() throws IOException, InterruptedException {
  // loadDataBrowserpage();
  // WebElement textArea = getWebDriver().findElement(By.id("dataBrowserQueryText"));
  // textArea.sendKeys("query1");
  // WebElement executeButton = getWebDriver().findElement(By.id("btnExecuteQuery"));
  // executeButton.click();
  // String QueryResultHeader1 = getWebDriver()
  // .findElement(By.xpath("//div[@id='clusterDetails']/div/div/span[@class='n-title']"))
  // .getText();
  // double count = 0, countBuffer = 0, countLine = 0;
  // String lineNumber = "";
  // String filePath =
  // "E:\\springsource\\springsourceWS\\Pulse-Cedar\\src\\main\\resources\\testQueryResultSmall.txt";
  // BufferedReader br;
  // String line = "";
  // br = new BufferedReader(new FileReader(filePath));
  // while ((line = br.readLine()) != null) {
  // countLine++;
  // String[] words = line.split(" ");
  //
  // for (String word : words) {
  // if (word.equals(QueryResultHeader1)) {
  // count++;
  // countBuffer++;
  // }
  // }
  // }
  // }
  //
  // public void testTreeMapPopUpData(String S1, String gridIcon) {
  // for (int i = 1; i <= 3; i++) {
  // searchByLinkAndClick(CLUSTER_VIEW_LABEL);
  // if (gridIcon.equals(SERVER_GROUP_GRID_ID)) {
  // WebElement ServerGroupRadio =
  // getWebDriver().findElement(By.xpath("//label[@for='radio-servergroups']"));
  // ServerGroupRadio.click();
  // }
  // if (gridIcon.equals(REDUNDANCY_GRID_ID)) {
  // WebElement ServerGroupRadio =
  // getWebDriver().findElement(By.xpath("//label[@for='radio-redundancyzones']"));
  // ServerGroupRadio.click();
  // }
  // searchByIdAndClick(gridIcon);
  // WebElement TreeMapMember =
  // getWebDriver().findElement(By.xpath("//div[@id='" + S1 + "M" + (i) + "']/div"));
  // Actions builder = new Actions(getWebDriver());
  // builder.clickAndHold(TreeMapMember).perform();
  // int j = 1;
  // String CPUUsageM1temp = getWebDriver()
  // .findElement(By.xpath("//div[@id='_tooltip']/div/div/div[2]/div/div[2]/div")).getText();
  // String CPUUsageM1 = CPUUsageM1temp.replaceAll("[\\%]", "");
  // String cpuUsageM1 = JMXProperties.getInstance().getProperty("member.M" + (i) + ".cpuUsage");
  // assertEquals(cpuUsageM1, CPUUsageM1);
  //
  // String MemoryUsageM1temp = getWebDriver()
  // .findElement(
  // By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[" + (j + 1) + "]/div[2]/div"))
  // .getText();
  // String MemoryUsageM1 = MemoryUsageM1temp.replaceAll("MB", "");
  // String memoryUsageM1 =
  // JMXProperties.getInstance().getProperty("member.M" + (i) + ".UsedMemory");
  // assertEquals(memoryUsageM1, MemoryUsageM1);
  //
  // String LoadAvgM1 = getWebDriver()
  // .findElement(
  // By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[" + (j + 2) + "]/div[2]/div"))
  // .getText();
  // String loadAvgM1 = JMXProperties.getInstance().getProperty("member.M" + (i) + ".loadAverage");
  // assertEquals(new DecimalFormat(PulseConstants.DECIMAL_FORMAT_PATTERN)
  // .format(Double.valueOf(loadAvgM1)), LoadAvgM1);
  //
  // String ThreadsM1 = getWebDriver()
  // .findElement(
  // By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[" + (j + 3) + "]/div[2]/div"))
  // .getText();
  // String threadsM1 = JMXProperties.getInstance().getProperty("member.M" + (i) + ".numThreads");
  // assertEquals(threadsM1, ThreadsM1);
  //
  // String SocketsM1 = getWebDriver()
  // .findElement(
  // By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[" + (j + 4) + "]/div[2]/div"))
  // .getText();
  // String socketsM1 =
  // JMXProperties.getInstance().getProperty("member.M" + (i) + ".totalFileDescriptorOpen");
  // assertEquals(socketsM1, SocketsM1);
  // builder.moveToElement(TreeMapMember).release().perform();
  // }
  // }
  //
  // @Test
  // public void testTopologyPopUpData() {
  // testTreeMapPopUpData("", CLUSTER_VIEW_GRID_ID);
  // }
  //
  // @Test
  // public void testServerGroupTreeMapPopUpData() {
  // testTreeMapPopUpData("SG1(!)", SERVER_GROUP_GRID_ID);
  // }
  //
  // @Test
  // public void testDataViewTreeMapPopUpData() {
  // searchByLinkAndClick(CLUSTER_VIEW_LABEL);
  // searchByLinkAndClick(DATA_DROPDOWN_ID);
  // WebElement TreeMapMember = getWebDriver().findElement(By.id("GraphTreeMapClusterData-canvas"));
  // Actions builder = new Actions(getWebDriver());
  // builder.clickAndHold(TreeMapMember).perform();
  // String RegionType = getWebDriver()
  // .findElement(By.xpath("//div[@id='_tooltip']/div/div/div[2]/div/div[2]/div")).getText();
  // String regionType = JMXProperties.getInstance().getProperty("region.R2.regionType");
  // assertEquals(regionType, RegionType);
  //
  // String EntryCount = getWebDriver()
  // .findElement(By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[2]/div[2]/div")).getText();
  // String entryCount =
  // JMXProperties.getInstance().getProperty("region.R2.systemRegionEntryCount");
  // assertEquals(entryCount, EntryCount);
  //
  // String EntrySizetemp = getWebDriver()
  // .findElement(By.xpath("//div[@id='_tooltip']/div/div/div[2]/div[3]/div[2]/div")).getText();
  // float EntrySize = Float.parseFloat(EntrySizetemp);
  // float entrySize =
  // Float.parseFloat(JMXProperties.getInstance().getProperty("region.R2.entrySize"));
  // entrySize = entrySize / 1024 / 1024;
  // entrySize = Float.parseFloat(new DecimalFormat("##.####").format(entrySize));
  // assertEquals(entrySize, EntrySize, 0.001);
  // builder.moveToElement(TreeMapMember).release().perform();
  // }
  //
  // @Test
  // public void testRegionViewTreeMapPopUpData() {
  // searchByLinkAndClick(CLUSTER_VIEW_LABEL);
  // searchByLinkAndClick(DATA_DROPDOWN_ID);
  // WebElement TreeMapMember = getWebDriver().findElement(By.id("GraphTreeMapClusterData-canvas"));
  // TreeMapMember.click();
  // }
  //
  // @Ignore("WIP")
  // @Test
  // public void testNumberOfRegions() throws InterruptedException {
  //
  // getWebDriver().findElement(By.xpath("//a[text()='Data Browser']")).click();
  //
  // Thread.sleep(1000);
  // List<WebElement> regionList = getWebDriver().findElements(By.xpath("//ul[@id='treeDemo']/li"));
  // String regions = JMXProperties.getInstance().getProperty("regions");
  // String[] regionName = regions.split(" ");
  // for (String string : regionName) {
  // }
  // // JMXProperties.getInstance().getProperty("region.R1.regionType");
  // int i = 1;
  // for (WebElement webElement : regionList) {
  // // webElement.getAttribute(arg0)
  // i++;
  // }
  //
  // getWebDriver().findElement(By.id("treeDemo_1_check")).click();
  //
  // List<WebElement> memeberList =
  // getWebDriver().findElements(By.xpath("//ul[@id='membersList']/li"));
  // int j = 0;
  // for (WebElement webElement : memeberList) {
  // j++;
  // }
  // }
}
