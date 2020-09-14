package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SiteMapPage;

public class SiteMapToolsDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageHasToolsSection() {
        Section toolsSection = siteMapPage.getToolsSection();

        assertThat(toolsSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageToolsSectionItemCountCorrect() {
        final int EXPECTED_COUNT = 14;

        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageCollectionActionsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Collection Actions";
        final String EXPECTED_LINK = Urls.Tools.COLLECTION_ACTIONS;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageCommanderLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Commander";
        final String EXPECTED_LINK = Urls.Tools.COMMANDER;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageCreateScheduleLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Create Schedule";
        final String EXPECTED_LINK = Urls.Tools.CREATE_SCHEDULE;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDataExportLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Export";
        final String EXPECTED_LINK = Urls.Tools.DATA_EXPORTER;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDataStreamingLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Streaming";
        final String EXPECTED_LINK = Urls.Tools.DATA_STREAMING;
        final int POSITION = 4;
        
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDataViewerLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Viewer";
        final String EXPECTED_LINK = Urls.Tools.DATA_VIEWER;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDeviceConfigurationLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Configuration";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDeviceConfigurationSummaryLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Configuration Summary";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION_SUMMARY;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDeviceGroupUploadLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Group Upload";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP_UPLOAD;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageDeviceGroupsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Groups";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageNotesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Notes";
        final String EXPECTED_LINK = Urls.Tools.NOTES;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
//    public void siteMapToolsDetails_pagePowerSupplierLoadsLinkCorrect() {
//        softly = new SoftAssertions();
//        final String EXPECTED_ANCHOR = "Power Supplier Loads";
//        final String EXPECTED_LINK = Urls.Tools.POWER_SUPPLIER_LOADS;
//        final int POSITION = 10;
//
//        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//        softly.assertAll();
//    }
//
//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
//    public void siteMapToolsDetails_pageProbabilityForPeakLoadLinkCorrect() {
//        softly = new SoftAssertions();
//        final String EXPECTED_ANCHOR = "Probability For Peak Load";
//        final String EXPECTED_LINK = Urls.Tools.PROBABILITY_PEAK_LOAD;
//        final int POSITION = 11;
//
//        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//        softly.assertAll();
//    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageRecentResultsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Recent Results";
        final String EXPECTED_LINK = Urls.Tools.RECENT_RESULTS;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageSchedulesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = Urls.Tools.SCHEDULES;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TOOLS })
    public void siteMapToolsDetails_pageScriptsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Scripts";
        final String EXPECTED_LINK = Urls.Tools.SCRIPTS;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
