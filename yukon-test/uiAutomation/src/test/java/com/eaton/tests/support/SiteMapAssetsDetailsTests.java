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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_AssetsSection_Displayed() {
        Section assetsSection = siteMapPage.getAssetsSection();

        assertThat(assetsSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_AssetsSection_CountCorrect() {
        final int EXPECTED_COUNT = 18;

        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_AssetsDashboard_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Assets Dashboard";
        final String EXPECTED_LINK = Urls.Assets.DASHBOARD;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_CommChannels_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Comm Channels";
        final String EXPECTED_LINK = Urls.Assets.COMM_CHANNELS_LIST;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_ComprehensiveMap_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Comprehensive Map";
        final String EXPECTED_LINK = Urls.Assets.COMPREHENSIVE_MAP;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_CreateAccount_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Create Account";
        final String EXPECTED_LINK = Urls.Assets.CREATE_ACCOUNT;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_Gateways_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Gateways";
        final String EXPECTED_LINK = Urls.Assets.GATEWAYS;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_Import_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Import";
        final String EXPECTED_LINK = Urls.Assets.IMPORT;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_OptOutStatus_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Opt Out Status";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_STATUS;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_OptOutSurveys_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Opt Out Surveys";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_SURVEYS;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_Purchasing_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Purchasing";
        //final String EXPECTED_LINK = Urls.Assets.PURCHASING;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_Relays_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Relays";
        final String EXPECTED_LINK = Urls.Assets.RELAYS;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_RTUs_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "RTUs";
        final String EXPECTED_LINK = Urls.Assets.RTUS;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_ServiceOrderList_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Service Order List";
        final String EXPECTED_LINK = Urls.Assets.SERVICE_ORDER_LIST;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_StarsReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "STARS Reports";
        final String EXPECTED_LINK = Urls.Assets.REPORTS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_ViewBatchCommands_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "View Batch Commands";
        final String EXPECTED_LINK = Urls.Assets.VIEW_BATCH_COMMANDS;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_VirtualDevices_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Virtual Devices";
        final String EXPECTED_LINK = Urls.Assets.VIRTUAL_DEVICES;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_WorkOrderReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Work Order Reports";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDER_REPORTS;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_WorkOrders_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Work Orders";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDERS;
        final int POSITION = 16;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ASSETS })
    public void siteMatpAssetsDetails_ZigBeeProblemDevices_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "ZigBee Problem Devices";
        final String EXPECTED_LINK = Urls.Assets.ZIGBEE_PROBLEM_DEVICES;
        final int POSITION = 17;

        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}