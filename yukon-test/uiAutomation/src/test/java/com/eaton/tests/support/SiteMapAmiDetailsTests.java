package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
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
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.SITE_MAP);

        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageHasAMISection() {
        Section amiSection = siteMapPage.getAMISection();

        assertThat(amiSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageAMISectionItemCountCorrect() {
        final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageAMIDashboardLinkCorrect() {
        final String EXPECTED_ANCHOR = "AMI Dashboard";
        final String EXPECTED_LINK = Urls.Ami.DASHBOARD;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageArchiveDataAnalysisLinkCorrect() {
        final String EXPECTED_ANCHOR = "Archive Data Analysis";
        final String EXPECTED_LINK = Urls.Ami.ARCHIVE_DATA_ANALYSIS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageBillingLinkCorrect() {
        final String EXPECTED_ANCHOR = "Billing";
        final String EXPECTED_LINK = Urls.Ami.BILLING;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageBillingSchedulesLinkCorrect() {
        final String EXPECTED_ANCHOR = "Billing Schedules";
        final String EXPECTED_LINK = Urls.Ami.BILLING_SCHEDULES;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageBulkImportLinkCorrect() {
        final String EXPECTED_ANCHOR = "Bulk Import";
        final String EXPECTED_LINK = Urls.Ami.BULK_IMPORT;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageAMIBulkUpdateLinkCorrect() {
        final String EXPECTED_ANCHOR = "Bulk Update";
        final String EXPECTED_LINK = Urls.Ami.BULK_UPDATE;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageManageDashboardsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Manage Dashboards";
        final String EXPECTED_LINK = Urls.Ami.MANAGE_DASHBOARDS;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageMeterEventsReportLinkCorrect() {
        final String EXPECTED_ANCHOR = "Meter Events Report";
        final String EXPECTED_LINK = Urls.Ami.METER_EVENTS_REPORTS;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageMeterProgrammingLinkCorrect() {
        final String EXPECTED_ANCHOR = "Meter Programming";
        // final String EXPECTED_LINK = Urls.Ami.METER_PROGRAMMING;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        // softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageMeterReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Metering Reports";
        final String EXPECTED_LINK = Urls.Ami.REPORTS;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pagePhaseDetectionLinkCorrect() {
        final String EXPECTED_ANCHOR = "Phase Detection";
        final String EXPECTED_LINK = Urls.Ami.PHASE_DETECTION;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageAMIPointImportLinkCorrect() {
        final String EXPECTED_ANCHOR = "Point Import";
        final String EXPECTED_LINK = Urls.Ami.POINT_IMPORT;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageReviewFlaggedPointsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Review Flagged Points";
        final String EXPECTED_LINK = Urls.Ami.REVIEW_FLAGGED_POINTS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageTrendsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Trends";
        final String EXPECTED_LINK = Urls.Tools.TRENDS;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageUsageThresholdReportLinkCorrect() {
        final String EXPECTED_ANCHOR = "Usage Threshold Report";
        final String EXPECTED_LINK = Urls.Ami.USAGE_THRESHOLD_REPORT;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void siteMapAmiDetails_pageWaterLeakReportLinkCorrect() {
        final String EXPECTED_ANCHOR = "Water Leak Report";
        final String EXPECTED_LINK = Urls.Ami.WATER_LEAK_REPORT;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION))
                .isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
