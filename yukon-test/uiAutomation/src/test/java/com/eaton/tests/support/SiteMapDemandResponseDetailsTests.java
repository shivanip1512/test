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

public class SiteMapDemandResponseDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL })
    public void siteMapDemandResponseDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    // ================================================================================
    // Demand Response Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_DrSection_Displayed() {
        Section drSection = siteMapPage.getDRSection();

        assertThat(drSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_DRSection_CountCorrect() {
        final int EXPECTED_COUNT = 13;

        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_ActiveControlAreas_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Active Control Areas";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_CONTROL_AREAS;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_ActiveLoadGroups_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Active Load Groups";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_LOAD_GROUPS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_ActivePrograms_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Active Programs";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_PROGRAMS;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_DRBulkUpdate_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Bulk Update";
        final String EXPECTED_LINK = Urls.DemandResponse.BULK_UPDATE;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_CICurtailment_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "C&I Curtailment";
        final String EXPECTED_LINK = Urls.DemandResponse.CI_CURTAILMENT;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_ControlAreas_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Control Areas";
        final String EXPECTED_LINK = Urls.DemandResponse.CONTROL_AREA;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_DRDashboard_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "DR Dashboard";
        final String EXPECTED_LINK = Urls.DemandResponse.DASHBOARD;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
//    public void siteMapDemandResponseDetails_EstimatedLoad_LinkCorrect() {
//        softly = new SoftAssertions();
//        final String EXPECTED_ANCHOR = "Estimated Load";
//        final String EXPECTED_LINK = Urls.DemandResponse.ESTIMATE_LOAD;
//        final int POSITION = 7;
//
//        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//        softly.assertAll();
//    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_LoadGroups_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Load Groups";
        final String EXPECTED_LINK = Urls.DemandResponse.LOAD_GROUPS;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_LoadManagementReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Load Management Reports";
        final String EXPECTED_LINK = Urls.DemandResponse.REPORTS;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_OddsForControl_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Odds for Control";
        final String EXPECTED_LINK = Urls.DemandResponse.ODDS_FOR_CONTROL;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_Programs_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Programs";
        final String EXPECTED_LINK = Urls.DemandResponse.PROGRAMS;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_Scenarios_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Scenarios";
        final String EXPECTED_LINK = Urls.DemandResponse.SCENARIOS;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void siteMapDemandResponseDetails_Setup_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Setup";
        final String EXPECTED_LINK = Urls.DemandResponse.SETUP;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
