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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_ToolsSection_Displayed() {
        Section toolsSection = siteMapPage.getToolsSection();

        assertThat(toolsSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_ToolsSection_CountCorrect() {
        final int EXPECTED_COUNT = 15;

        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_AggregateIntervalDataReport_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Aggregate Interval Data Report";
        final String EXPECTED_LINK = Urls.Tools.AGGREGATE_INTERVAL_DATA_REPORT;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_CollectionActions_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Collection Actions";
        final String EXPECTED_LINK = Urls.Tools.COLLECTION_ACTIONS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_Commander_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Commander";
        final String EXPECTED_LINK = Urls.Tools.COMMANDER;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_CreateSchedule_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Create Schedule";
        final String EXPECTED_LINK = Urls.Tools.CREATE_SCHEDULE;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DataExport_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Export";
        final String EXPECTED_LINK = Urls.Tools.DATA_EXPORTER;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DataStreaming_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Streaming";
        final String EXPECTED_LINK = Urls.Tools.DATA_STREAMING;
        final int POSITION = 5;
        
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DataViewer_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Data Viewer";
        final String EXPECTED_LINK = Urls.Tools.DATA_VIEWER;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DeviceConfiguration_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Configuration";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DeviceConfigurationSummary_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Configuration Summary";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION_SUMMARY;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DeviceGroupUpload_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Group Upload";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP_UPLOAD;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_DeviceGroups_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Groups";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_Notes_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Notes";
        final String EXPECTED_LINK = Urls.Tools.NOTES;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_RecentResults_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Recent Results";
        final String EXPECTED_LINK = Urls.Tools.RECENT_RESULTS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_Schedules_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = Urls.Tools.SCHEDULES;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TOOLS })
    public void siteMapToolsDetails_Scripts_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Scripts";
        final String EXPECTED_LINK = Urls.Tools.SCRIPTS;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
