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

public class SiteMapAssetsDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageHasAssetsSection() {
        Section assetsSection = siteMapPage.getAssetsSection();

        assertThat(assetsSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageAssetsSectionItemCountCorrect() {
        final int EXPECTED_COUNT = 18;

        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageAssetsDashboardLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Assets Dashboard";
        final String EXPECTED_LINK = Urls.Assets.DASHBOARD;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageCommChannelsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Comm Channels";
        final String EXPECTED_LINK = Urls.Assets.COMM_CHANNELS_LIST;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageComprehensiveMapLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Comprehensive Map";
        final String EXPECTED_LINK = Urls.Assets.COMPREHENSIVE_MAP;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageCreateAccountLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Create Account";
        final String EXPECTED_LINK = Urls.Assets.CREATE_ACCOUNT;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageGatewaysLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Gateways";
        final String EXPECTED_LINK = Urls.Assets.GATEWAYS;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageImportLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Import";
        final String EXPECTED_LINK = Urls.Assets.IMPORT;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageOptOutStatusLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Opt Out Status";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_STATUS;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageOptOutSurveysLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Opt Out Surveys";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_SURVEYS;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pagePurchasingLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Purchasing";
        //final String EXPECTED_LINK = Urls.Assets.PURCHASING;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageRelaysLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Relays";
        final String EXPECTED_LINK = Urls.Assets.RELAYS;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageRTUsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "RTUs";
        final String EXPECTED_LINK = Urls.Assets.RTUS;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageServiceOrderListLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Service Order List";
        final String EXPECTED_LINK = Urls.Assets.SERVICE_ORDER_LIST;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageSTARSReportsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "STARS Reports";
        final String EXPECTED_LINK = Urls.Assets.REPORTS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageViewBatchCommandsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "View Batch Commands";
        final String EXPECTED_LINK = Urls.Assets.VIEW_BATCH_COMMANDS;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageVirtualDevicesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Virtual Devices";
        final String EXPECTED_LINK = Urls.Assets.VIRTUAL_DEVICES;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageWorkOrderReportsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Work Order Reports";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDER_REPORTS;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageWorkOrdersLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Work Orders";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDERS;
        final int POSITION = 16;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.ASSETS })
    public void siteMatpAssetsDetails_pageZigBeeProblemDevicesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "ZigBee Problem Devices";
        final String EXPECTED_LINK = Urls.Assets.ZIGBEE_PROBLEM_DEVICES;
        final int POSITION = 17;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}