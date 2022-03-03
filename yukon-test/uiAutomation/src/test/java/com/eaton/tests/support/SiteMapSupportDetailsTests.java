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

public class SiteMapSupportDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_SupportSection_Displayed() {
        Section supportSection = siteMapPage.getSupportSection();

        assertThat(supportSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_SupportSection_CountCorrect() {
        final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_DataStreamingDeviceAttributes_LinkCorrect() {
        softly = new SoftAssertions();
    	final String EXPECTED_ANCHOR = "Data Streaming Device Attributes";
        final String EXPECTED_LINK = Urls.Support.DATA_STREAMING_DEVICE_ATTRIBUTES;
        final int POSITION = 0;
    	
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_DatabaseMigration_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Database Migration";
        final String EXPECTED_LINK = Urls.Support.DATABASE_MIGRATION;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_DatabaseValidation_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Database Validation";
        final String EXPECTED_LINK = Urls.Support.DATABASE_VALIDATION;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_DeviceDefinitions_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Device Definitions";
        final String EXPECTED_LINK = Urls.Support.DEVICE_DEFINITIONS;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_ErrorCodes_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Error Codes";
        final String EXPECTED_LINK = Urls.Support.ERROR_CODES;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_EventLog_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Event Log";
        final String EXPECTED_LINK = Urls.Support.EVENT_LOG;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_FileExportHistory_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "File Export History";
        final String EXPECTED_LINK = Urls.Support.FILE_EXPORT_HISTORY;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_LocalizationHelper_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Localization Helper";
        final String EXPECTED_LINK = Urls.Support.LOCALIZATION_HELPER;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_LogExplorer_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Log Explorer";
        final String EXPECTED_LINK = Urls.Support.LOG_EXPLORER;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_ManageIndexes_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Manage Indexes";
        final String EXPECTED_LINK = Urls.Support.MANAGE_INDEXES;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_RouteUsage_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Route Usage";
        final String EXPECTED_LINK = Urls.Support.ROUTE_USAGE;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_Support_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Support";
        final String EXPECTED_LINK = Urls.SUPPORT;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_SystemHealth_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "System Health";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_HEALTH;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_SystemInfo_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "System Info";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_INFO;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_ThirdPartyLibraries_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Third Party Libraries";
        final String EXPECTED_LINK = Urls.Support.THIRD_PARTY_LIBRARIES;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.SUPPORT })
    public void siteMapSupportDetails_ThreadDump_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Thread Dump";
        final String EXPECTED_LINK = Urls.Support.THREAD_DUMP;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
