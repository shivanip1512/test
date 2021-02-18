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

public class SiteMapAmiDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_AMISection_Displayed() {
        Section amiSection = siteMapPage.getAMISection();

        assertThat(amiSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_AMISection_CountCorrect() {
        final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_AMIDashboard_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "AMI Dashboard";
        final String EXPECTED_LINK = Urls.Ami.DASHBOARD;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_ArchiveDataAnalysis_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Archive Data Analysis";
        final String EXPECTED_LINK = Urls.Ami.ARCHIVE_DATA_ANALYSIS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_Billing_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Billing";
        final String EXPECTED_LINK = Urls.Ami.BILLING;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_BillingSchedules_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Billing Schedules";
        final String EXPECTED_LINK = Urls.Ami.BILLING_SCHEDULES;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_BulkImport_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Bulk Import";
        final String EXPECTED_LINK = Urls.Ami.BULK_IMPORT;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_AMIBulkUpdate_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Bulk Update";
        final String EXPECTED_LINK = Urls.Ami.BULK_UPDATE;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_ManageDashboards_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Manage Dashboards";
        final String EXPECTED_LINK = Urls.Ami.MANAGE_DASHBOARDS;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_MeterEventsReport_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Meter Events Report";
        //final String EXPECTED_LINK = Urls.Ami.METER_EVENTS_REPORTS;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_MeterProgramming_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Meter Programming";
        final String EXPECTED_LINK = Urls.Ami.METER_PROGRAMMING;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_MeterReports_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Metering Reports";
        final String EXPECTED_LINK = Urls.Ami.REPORTS;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_PhaseDetection_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Phase Detection";
        final String EXPECTED_LINK = Urls.Ami.PHASE_DETECTION;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_AMIPointImport_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Point Import";
        final String EXPECTED_LINK = Urls.Ami.POINT_IMPORT;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_ReviewFlaggedPoints_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Review Flagged Points";
        final String EXPECTED_LINK = Urls.Ami.REVIEW_FLAGGED_POINTS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_Trends_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Trends";
        final String EXPECTED_LINK = Urls.Tools.TRENDS_LIST;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_UsageThresholdReport_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Usage Threshold Report";
        final String EXPECTED_LINK = Urls.Ami.USAGE_THRESHOLD_REPORT;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void siteMapAmiDetails_WaterLeakReport_LinkCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Water Leak Report";
        final String EXPECTED_LINK = Urls.Ami.WATER_LEAK_REPORT;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
