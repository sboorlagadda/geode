/// *
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
// * agreements. See the NOTICE file distributed with this work for additional information regarding
// * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0
/// (the
// * "License"); you may not use this file except in compliance with the License. You may obtain a
// * copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the
/// License
// * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
/// express
// * or implied. See the License for the specific language governing permissions and limitations
/// under
// * the License.
// *
// */
/// **
// * This test class contains automated tests for Pulse application related to 1. Different grid
/// data
// * validations for example - Topology, Server Group, Redundancy Zone 2. Data Browser 3.
// *
// * @since GemFire 2014-04-02
// */
// package org.apache.geode.tools.pulse.ui;
//
//
//
//
//
// @Category(UITest.class)
// public class PulseAutomatedTest extends PulseBase {
//
// @ClassRule
// public static LocatorStarterRule locator =
// new LocatorStarterRule().withJMXManager().withAutoStart();
//
// @Rule
// public WebDriverRule webDriverRule = new WebDriverRule("admin", "admin", getPulseURL());
//
// @Rule
// public ScreenshotOnFailureRule screenshotOnFailureRule =
// new ScreenshotOnFailureRule(this::getWebDriver);
//
// private Cluster cluster;
//
// @Override
// public WebDriver getWebDriver() {
// return webDriverRule.getDriver();
// }
//
// @Override
// public String getPulseURL() {
// return "http://localhost:" + locator.getHttpPort() + "/pulse/";
// }
//
// @Override
// public Cluster getCluster() {
// return this.cluster;
// }
//
// @Test
// public void serverGroupGridDataValidation() {
// PulseTestUtils.navigateToServerGroupGridView();
// PulseTestUtils.validateServerGroupGridData();
// }
//
// @Test
// public void redundancyZonesGridDataValidation() {
// PulseTestUtils.navigateToRedundancyZonesGridView();
// PulseTestUtils.validateRedundancyZonesGridData();
// }
//
// @Test
// public void topologyGridDataValidation() {
// PulseTestUtils.navigateToTopologyGridView();
// PulseTestUtils.validateTopologyGridData();
// }
//
// @Test
// public void dataViewGridDataValidation() {
// PulseTestUtils.navigateToDataPrespectiveGridView();
// PulseTestUtils.validateDataPrespectiveGridData();
// }
//
// @Test
// public void regionDetailsGridDataValidation() {
// PulseTestUtils.navigateToRegionDetailsGridView();
// PulseTestUtils.validateRegionDetailsGridData();
//
// }
//
// @Test
// public void regionDetailsNavigationTest() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals("/R2",
/// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionNameDivId));
// }
//
// @Test
// public void regionName() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.name"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionNameDivId));
// }
//
// @Test
// public void regionPath() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.fullPath"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionPathId));
// }
//
// @Test
// public void regionType() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.regionType"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionTypeId));
// }
//
// @Test
// public void regionMembers() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.memberCount"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionMembersTextId));
// }
//
// @Test
// public void regionEmptyNodes() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.emptyNodes"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionEmptyNodesId));
// }
//
// @Test
// public void regionEntryCount() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.systemRegionEntryCount"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionEntryCountTextId));
// }
//
// @Test
// public void regionDiskUsage() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.diskUsage"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionDiskUsageId));
// }
//
// @Test
// public void regionPersistence() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPersistanceEnabled(PulseTestUtils.getPropertyValue("region.R2.persistentEnabled")),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.regionPersistenceId));
// }
//
// @Ignore("WIP")
// @Test
// public void regionMemoryUsage() {
// PulseTestUtils.navigateToRegionDetailsView();
// // need to check the respective property values
// }
//
// @Test
// public void regionInMemoryRead() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.getsRate"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.inMemoryReadsId));
//
// }
//
// @Test
// public void regionInMemoryWrites() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.putsRate"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.inMemoryWritesId));
// }
//
// @Test
// public void regionDiskRead() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.diskReadsRate"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.diskReadsId));
// }
//
// @Test
// public void regionDiskWrites() {
// PulseTestUtils.navigateToRegionDetailsView();
// Assert.assertEquals(PulseTestUtils.getPropertyValue("region.R2.diskWritesRate"),
// PulseTestUtils.getTextUsingId(PulseTestLocators.RegionDetailsView.diskWritesId));
// }
//
// @Test
// public void clickHostShowsMemberTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.waitForElementWithId(PulseTestLocators.TopologyView.nodeH1Id).click();
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.waitForElementWithId(PulseTestLocators.TopologyView.nodeH2Id).click();
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.waitForElementWithId(PulseTestLocators.TopologyView.nodeH3Id).click();
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM3Id);
// }
//
// @Test
// @Ignore("Issue with highlighting")
// public void verifyHostTooltipsOfTopologyGraphTest() {
// for (int i = 1; i <= 3; i++) {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.mouseClickAndHoldOverElementById("h" + i);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.hostNameTTXpath,
// PulseTestUtils.getPropertyValue("member.M" + i + ".host"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.cpuUsageTTXpath, "0%");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memoryUsageTTXpath,
// PulseTestUtils.getPropertyValue("member.M" + i + ".UsedMemory"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.loadAvgTTXpath,
// PulseTestUtils.getPropertyValue("member.M" + i + ".loadAverage"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.soketsTTXpath,
// PulseTestUtils.getPropertyValue("member.M" + i + ".totalFileDescriptorOpen"));
// PulseTestUtils.mouseReleaseById("h" + i);
// webDriverRule.getDriver().navigate().refresh();
// }
// }
//
// @Ignore("Issues with member tooltip xpath")
// @Test
// public void verifyMemberTooltipsOfTopologyGraphTest() {
//
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH1Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH1Id);
// PulseTestUtils.mouseClickAndHoldOverElementById(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memNameTTXpath,
// PulseTestUtils.getPropertyValue("member.M1.member"));
// // verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memCpuUsageTTXpath,
// // getPropertyValue("member.M1.cpuUsage") + "%");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.jvmPausesTTXpath,
// PulseTestUtils.getPropertyValue("member.M1.JVMPauses"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.regionsTTXpath,
// PulseTestUtils.getPropertyValue("member.M1.totalRegionCount"));
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH2Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH2Id);
// PulseTestUtils.mouseClickAndHoldOverElementById(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memNameTTXpath,
// PulseTestUtils.getPropertyValue("member.M2.member"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memCpuUsageTTXpath,
// PulseTestUtils.getPropertyValue("member.M2.cpuUsage") + "%");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.jvmPausesTTXpath,
// PulseTestUtils.getPropertyValue("member.M2.JVMPauses"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.regionsTTXpath,
// PulseTestUtils.getPropertyValue("member.M2.totalRegionCount"));
//
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH3Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH3Id);
// PulseTestUtils.mouseClickAndHoldOverElementById(PulseTestLocators.TopologyView.memberM3Id);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memNameTTXpath,
// PulseTestUtils.getPropertyValue("member.M3.member"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.memCpuUsageTTXpath,
// PulseTestUtils.getPropertyValue("member.M3.cpuUsage") + "%");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.jvmPausesTTXpath,
// PulseTestUtils.getPropertyValue("member.M3.JVMPauses"));
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.regionsTTXpath,
// PulseTestUtils.getPropertyValue("member.M3.totalRegionCount"));
//
// }
//
// @Test
// public void VerifyRGraphTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH2Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.nodeH3Id);
// }
//
// @Test
// @Ignore("ElementNotVisible with phantomJS")
// public void clickMembersOfTopologyGraphTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH1Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH2Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.nodeH3Id);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
// @Test
// public void clickTreeMapViewShowingTreeMapTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM3Id);
// }
//
// @Test
// public void verifyMembersPresentInTreeMapTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM1Id, "M1");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM2Id, "M2");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM3Id, "M3");
// }
//
// @Test
// public void clickMemberNavigatingToCorrespondingRegionTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM1Id, "M1");
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM2Id, "M2");
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.memberM3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.TopologyView.memberM3Id, "M3");
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.memberM3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
// @Test
// public void clickGridButtonShowsGridTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.idM1Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM1Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH1Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.idM2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.idM3Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM3Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH3Xpath);
// }
//
// @Test
// public void verifyMembersPresentInGridTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM1Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.nameM1Xpath, "M1");
//
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM2Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.nameM2Xpath, "M2");
//
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.nameM3Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.nameM3Xpath, "M3");
// }
//
// @Test
// public void verifyHostNamesInGridTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH1Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.hostH1Xpath, "h1");
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH2Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.hostH2Xpath, "h2");
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.TopologyView.hostH3Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.TopologyView.hostH3Xpath, "h3");
// }
//
// @Test
// public void clickOnGridMemNameNavigatingToCorrespondingRegionTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.nameM1Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
//
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.nameM2Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
//
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.gridButtonId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.nameM3Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
// @Test
// public void verifyMembersPresentInSvrGrpTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
//
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrpsRadioXpath);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.serverGrp1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.serverGrp2Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.serverGrp3Id);
//
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg1M1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg1M2Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg1M3Id);
//
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg2M1Id);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg2M2Id);
//
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.ServerGroups.sg3M3Id);
// }
//
// @Test
// @Ignore("ElementNotVisible with phantomJS")
// public void expandAndCloseServerGroupsTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// // waitForElement(findElementByXpath(PulseTestLocators.ServerGroups.serverGrpsRadioXpath));
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrpsRadioXpath);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp1Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp1Id, "style",
// "width: 720px; height: 415px;");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp1Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp1Id, "style",
// "width: 239.667px; height: 399px;");
//
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp2Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp2Id, "style",
// "width: 720px; height: 415px;");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp2Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp2Id, "style",
// "width: 239.667px; height: 399px;");
//
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp3Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp3Id, "style",
// "width: 720px; height: 415px;");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrp3Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.ServerGroups.serverGrp3Id, "style",
// "width: 239.667px; height: 399px;");
// }
//
// @Test
// public void verifyMembersInServGrpTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrpsRadioXpath);
//
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.serverGrp1Id, "SG1");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.serverGrp2Id, "SG2");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.serverGrp3Id, "SG3");
//
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg1M1Id, "M1");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg1M2Id, "M2");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg1M3Id, "M3");
//
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg2M1Id, "M1");
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg2M2Id, "M2");
//
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.ServerGroups.sg3M3Id, "M3");
// }
//
// @Test
// public void memberNavigationFromServGrpTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.serverGrpsRadioXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg1M1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg1M2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg1M3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg2M1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg2M2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.sg3M3Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
// @Test
// public void clickServGrpGridButtonShowsGridTest() {
// PulseTestUtils.navigateToServerGroupGridView();
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.idSG1M3Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.idSG1M2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.idSG1M1Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.nameM3Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.nameM2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.ServerGroups.nameM1Xpath);
//
// }
//
// @Test
// public void memberNavigationFromServGrpGridTest() {
// PulseTestUtils.navigateToServerGroupGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.idSG1M3Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// PulseTestUtils.navigateToServerGroupGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.idSG1M1Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.navigateToServerGroupGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.idSG1M2Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// }
//
// @Test
// public void verifyZonePresentTest() {
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.RedundancyZone.zoneRZ1RZ2Xpath);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.RedundancyZone.zoneRZ2Id);
// }
//
// @Test
// public void expandAndCloseRdncyZoneTest() {
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.zoneRZ1RZ2Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.RedundancyZone.zoneRZ1Id, "style",
// "width: 720px; height: 415px;");
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.zoneRZ1RZ2Xpath);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.zoneRZ2Xpath);
// PulseTestUtils.verifyElementAttributeById(PulseTestLocators.RedundancyZone.zoneRZ2Id, "style",
// "width: 720px; height: 415px;");
//
// }
//
// @Test
// public void clickRZMembersNavigationTest() {
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.m1RZ1RZ2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.m2RZ1Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.m3RZ2Id);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
// @Test
// public void clickRZGridShowingGridTest() {
// PulseTestUtils.navigateToRedundancyZonesGridView();
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.RedundancyZone.idM2Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.RedundancyZone.idM1Xpath);
// PulseTestUtils.verifyElementPresentByXpath(PulseTestLocators.RedundancyZone.idM3Xpath);
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.RedundancyZone.idM2Xpath, "M2");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.RedundancyZone.idM1Xpath, "M1");
// PulseTestUtils.verifyTextPresrntByXpath(PulseTestLocators.RedundancyZone.idM3Xpath, "M3");
// }
//
// @Test
// public void clickRZGridMembersNavigationTest() {
// PulseTestUtils.navigateToRedundancyZonesGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.idM2Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M2");
// PulseTestUtils.navigateToRedundancyZonesGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.idM1Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M1");
// PulseTestUtils.navigateToRedundancyZonesGridView();
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.idM3Xpath);
// PulseTestUtils.verifyTextPresrntById(PulseTestLocators.RegionDetailsView.memberNameId, "M3");
// }
//
//
// @Test
// public void verifySortingOptionsTest() {
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopNavigation.clusterViewLinkXpath);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.treeMapButtonId);
// PulseTestUtils.verifyElementPresentById(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.verifyElementPresentByLinkText("Heap Usage");
// PulseTestUtils.verifyElementPresentByLinkText("CPU Usage");
// }
//
// /*
// * HotSpot test scripts -
// */
// // --- Topology view
//
// @Test
// public void testHotSpotOptPrsntOnTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// Assert.assertEquals(PulseTestData.Topology.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.TopologyView.hotSpotId));
// }
//
// @Test
// public void testHotSpotOptionsTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// Assert.assertEquals(PulseTestData.Topology.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.TopologyView.heapUsageXpath));
// Assert.assertEquals(PulseTestData.Topology.hotSpotCPULbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.TopologyView.cpuUsageXpath));
// }
//
// @Test
// public void testCpuUsageNavigationOnTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.cpuUsageXpath);
// Assert.assertEquals(PulseTestData.Topology.hotSpotCPULbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.TopologyView.hotSpotId));
// }
//
// @Test
// public void testHeapUsageNavigationOnTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.heapUsageXpath);
// Assert.assertEquals(PulseTestData.Topology.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.TopologyView.hotSpotId));
// }
//
// @Test
// public void testSortingUsingCpuUsageOnTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.cpuUsageXpath);
// PulseTestUtils.assertMemberSortingByCpuUsage();
// }
//
// @Test
// public void testSortingUsingHeapUsageOnTopologyView() {
// PulseTestUtils.navigateToTopologyTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.TopologyView.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.TopologyView.heapUsageXpath);
// PulseTestUtils.assertMemberSortingByHeapUsage();
// }
//
// // --- Server Group view
//
// @Test
// public void testHotSpotOptPrsntOnServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// Assert.assertEquals(PulseTestData.ServerGroups.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.ServerGroups.hotSpotId));
// }
//
// @Test
// public void testHotSpotOptionsServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.hotSpotId);
// Assert.assertEquals(PulseTestData.ServerGroups.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.ServerGroups.heapUsageXpath));
// Assert.assertEquals(PulseTestData.ServerGroups.hotSpotCPULbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.ServerGroups.cpuUsageXpath));
// }
//
// @Test
// public void testCpuUsageNavigationOnServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.cpuUsageXpath);
// Assert.assertEquals(PulseTestData.ServerGroups.hotSpotCPULbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.ServerGroups.hotSpotId));
// }
//
// @Test
// public void testHeapUsageNavigationOnServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.heapUsageXpath);
// Assert.assertEquals(PulseTestData.ServerGroups.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.ServerGroups.hotSpotId));
// }
//
// @Test
// public void testSortingUsingHeapUsageOnServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.heapUsageXpath);
// PulseTestUtils.assertMemberSortingBySgHeapUsage();
// }
//
// @Test
// public void testSortingUsingCpuUsageOnServerGroupView() {
// PulseTestUtils.navigateToServerGroupTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.ServerGroups.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.ServerGroups.cpuUsageXpath);
// PulseTestUtils.assertMemberSortingBySgCpuUsage();
// }
//
// // --- Redundancy Zone view
//
// @Test
// public void testHotSpotOptPrsntOnRedundancyZoneView() {
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// Assert.assertEquals(PulseTestData.RedundancyZone.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.RedundancyZone.hotSpotId));
// }
//
//
// @Test
// public void testHotSpotOptionsRedundancyZoneView() {
// // navigate to Redundancy Zones - Tree View
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.hotSpotId);
// Assert.assertEquals(PulseTestData.RedundancyZone.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.RedundancyZone.heapUsageXpath));
// Assert.assertEquals(PulseTestData.RedundancyZone.hotSpotCPULbl,
// PulseTestUtils.getTextUsingXpath(PulseTestLocators.RedundancyZone.cpuUsageXpath));
// }
//
// @Test
// public void testCpuUsageNavigationOnRedundancyZoneView() {
// // navigate to Redundancy Zones - Tree View
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.cpuUsageXpath);
// Assert.assertEquals(PulseTestData.RedundancyZone.hotSpotCPULbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.RedundancyZone.hotSpotId));
// }
//
// @Test
// public void testHeapUsageNavigationOnRedundancyZoneView() {
// // navigate to Redundancy Zones - Tree View
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.heapUsageXpath);
// Assert.assertEquals(PulseTestData.RedundancyZone.hotSpotHeapLbl,
// PulseTestUtils.getTextUsingId(PulseTestLocators.RedundancyZone.hotSpotId));
// }
//
// @Test
// public void testSortingUsingHeapUsageOnRedundancyView() {
// // navigate to Redundancy Zones - Tree View
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.heapUsageXpath);
// PulseTestUtils.assertMemberSortingByRzHeapUsage();
// }
//
// @Test
// public void testSortingUsingCpuUsageOnRedundancyView() {
// // navigate to Redundancy Zones - Tree View
// PulseTestUtils.navigateToRedundancyZonesTreeView();
// PulseTestUtils.clickElementUsingId(PulseTestLocators.RedundancyZone.hotSpotId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.RedundancyZone.cpuUsageXpath);
// PulseTestUtils.assertMemeberSortingByRzCpuUsage();
// }
//
// @Test
// public void testDataBrowserFilterFeature() {
// // navigate to Data browser page
// loadDataBrowserpage();
// List<WebElement> regionLst = PulseTestUtils.getRegionsFromDataBrowser();
// String[] regionNames = new String[regionLst.size()];
// for (int regionIndex = 0; regionIndex < regionLst.size(); regionIndex++) {
// regionNames[regionIndex] =
/// PulseTestUtils.findElementByXpath(PulseTestLocators.DataBrowser.rgnSpanFirstPart
// + (regionIndex + 1) + PulseTestLocators.DataBrowser.rgnSpanSecondPart).getText();
// }
// // type each region name in region filter and verify respective region(s) are displayed in
// // region list
// for (String region : regionNames) {
// PulseTestUtils.waitForElementWithId(PulseTestLocators.DataBrowser.rgnFilterTxtBoxId).clear();
// PulseTestUtils.waitForElementWithId(PulseTestLocators.DataBrowser.rgnFilterTxtBoxId).sendKeys(region);
//
// List<WebElement> regionLst1 = PulseTestUtils.getRegionsFromDataBrowser();
//
// for (int regionIndex = 1; regionIndex <= regionLst1.size(); regionIndex++) {
// Assert.assertEquals(region,
/// PulseTestUtils.findElementByXpath(PulseTestLocators.DataBrowser.rgnSpanFirstPart
// + regionIndex + PulseTestLocators.DataBrowser.rgnSpanSecondPart).getText());
// }
// }
// }
//
// @Test
// public void testDataBrowserFilterPartialRegionName() {
// // navigate to Data browser page
// loadDataBrowserpage();
// PulseTestUtils.waitForElementWithId(PulseTestLocators.DataBrowser.rgnFilterTxtBoxId).clear();
//
// // type partial region name in region filter and verify that all the regions that contains that
// // text displays
// PulseTestUtils.waitForElementWithId(PulseTestLocators.DataBrowser.rgnFilterTxtBoxId)
// .sendKeys(PulseTestData.DataBrowser.partialRgnName);
// List<WebElement> regionLst = PulseTestUtils.getRegionsFromDataBrowser();
//
// for (int regionIndex = 0; regionIndex < regionLst.size(); regionIndex++) {
// assertTrue(PulseTestUtils.findElementByXpath(PulseTestLocators.DataBrowser.rgnSpanFirstPart
// + (regionIndex + 1) + PulseTestLocators.DataBrowser.rgnSpanSecondPart).getText()
// .contains(PulseTestData.DataBrowser.partialRgnName));
// }
// }
//
// @Test
// public void testDataBrowserClearButton() {
// // navigate to Data browser page
// loadDataBrowserpage();
//
// PulseTestUtils.sendKeysUsingId(PulseTestLocators.DataBrowser.queryEditorTxtBoxId,
// PulseTestData.DataBrowser.query1Text);
// String editorTextBeforeClear =
// PulseTestUtils.getTextUsingId(PulseTestLocators.DataBrowser.queryEditorTxtBoxId);
// PulseTestUtils.clickElementUsingXpath(PulseTestLocators.DataBrowser.btnClearXpath);
// String editorTextAfterClear =
/// PulseTestUtils.getTextUsingId(PulseTestLocators.DataBrowser.queryEditorTxtBoxId);
//
// assertFalse(PulseTestData.DataBrowser.query1Text.equals(editorTextAfterClear));
// }
//
// @Ignore("WIP") // Data Browser's Query History not showing any data on button click, therefore
// // this test is failing
// @Test
// public void testDataBrowserHistoryQueue() {
// // navigate to Data browser page
// loadDataBrowserpage();
//
// List<WebElement> numOfReg = webDriverRule.getDriver()
// .findElements(By.xpath(PulseTestLocators.DataBrowser.divDataRegions));
//
// for (int i = 1; i <= numOfReg.size(); i++) {
// if (PulseTestUtils.getTextUsingId("treeDemo_" + i +
/// "_span").equals(PulseTestData.DataBrowser.regName)) {
// searchByIdAndClick("treeDemo_" + i + "_check"); // driver.findElement(By.id("treeDemo_" + i
// // + "_check")).click();
// }
// }
//
// PulseTestUtils.sendKeysUsingId(PulseTestLocators.DataBrowser.queryEditorTxtBoxId,
// DataBrowserResultLoader.QUERY_TYPE_ONE);
// PulseTestUtils.clickElementUsingId(PulseTestLocators.DataBrowser.btnExecuteQueryId);
//
// // Get required datetime format and extract date and hours from date time.
// DateFormat dateFormat = new SimpleDateFormat(PulseTestData.DataBrowser.datePattern);
// String queryDateTime = dateFormat.format(System.currentTimeMillis());
// String queryTime[] = queryDateTime.split(":");
// System.out.println("Query Time from System: " + queryTime[0]);
//
//
// PulseTestUtils.clickElementUsingId(PulseTestLocators.DataBrowser.historyIcon);
// List<WebElement> historyLst =
// webDriverRule.getDriver().findElements(By.xpath(PulseTestLocators.DataBrowser.historyLst));
// String queryText = PulseTestUtils.findElementByXpath(PulseTestLocators.DataBrowser.historyLst)
// .findElement(By.cssSelector(PulseTestLocators.DataBrowser.queryText)).getText();
// String historyDateTime =
/// PulseTestUtils.findElementByXpath(PulseTestLocators.DataBrowser.historyLst)
// .findElement(By.cssSelector(PulseTestLocators.DataBrowser.historyDateTime)).getText();
// System.out.println("Query Text from History Table: " + queryText);
// System.out.println("Query Time from History Table: " + historyDateTime);
// // verify the query text, query datetime in history panel
// assertTrue(DataBrowserResultLoader.QUERY_TYPE_ONE.equals(queryText));
// assertTrue(historyDateTime.contains(queryTime[0]));
//
// }
//
//
// }
