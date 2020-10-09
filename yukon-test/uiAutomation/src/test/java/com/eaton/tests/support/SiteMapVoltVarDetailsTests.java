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

public class SiteMapVoltVarDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageHasCCSection() {
        Section ccSection = siteMapPage.getCCSection();

        assertThat(ccSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageCCSectionItemCountCorrect() {
        final int EXPECTED_COUNT = 10;

        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageCapControlImportLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "CapControl Import";
        final String EXPECTED_LINK = Urls.CapControl.IMPORT;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageCapControlReportsLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "CapControl Reports";
        final String EXPECTED_LINK = Urls.CapControl.REPORTS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageDMVTestLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "DMV Test";
        final String EXPECTED_LINK = Urls.CapControl.DMV_TEST_LIST;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageFDRTranslationManagementLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "FDR Translation Management";
        //final String EXPECTED_LINK = Urls.CapControl.FDR_TRANSLATION_MANAGER;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageOrphansLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Orphans";
        final String EXPECTED_LINK = Urls.CapControl.ORPHANS;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageCCPointImportLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Point Import";
        final String EXPECTED_LINK = Urls.CapControl.POINT_IMPORT;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageRecentTempCapBankMovesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Recent Temp Cap Bank Moves";
        final String EXPECTED_LINK = Urls.CapControl.RECENT_TEMP_MOVES;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageCCSchedulesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = Urls.CapControl.SCHEDULES;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageStrategiesLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Strategies";
        final String EXPECTED_LINK = Urls.CapControl.STRATEGIES;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.VoltVar.VOLT_VAR })
    public void siteMapVoltVarDetails_pageVoltVarDashboardLinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Volt/Var Dashboard";
        final String EXPECTED_LINK = Urls.CapControl.DASHBOARD;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}