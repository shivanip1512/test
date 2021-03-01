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

public class SiteMapAdminDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();        
        navigate(Urls.SITE_MAP);
        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_AdminSection_Displayed() {
        Section adminSection = siteMapPage.getAdminSection();

        assertThat(adminSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_AdminSection_CountCorrect() {
        final int EXPECTED_COUNT = 17;

        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_ActiveJobs_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Active Jobs";
        final String EXPECTED_LINK = Urls.Admin.ACTIVE_JOBS;
        final int POSITION = 0;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_AdministratorReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Administrator Reports";
        final String EXPECTED_LINK = Urls.Admin.REPORTS;
        final int POSITION = 1;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_AllJobs_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "All Jobs";
        final String EXPECTED_LINK = Urls.Admin.ALL_JOBS;
        final int POSITION = 2;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_CIReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Attributes";
        //final String EXPECTED_LINK = Urls.Admin.CI_REPORTS;
        final int POSITION = 3;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_Attributes_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "C&I Reports";
        final String EXPECTED_LINK = Urls.Admin.CI_REPORTS;
        final int POSITION = 4;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_DashboardAdmin_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Dashboard Administration";
        final String EXPECTED_LINK = Urls.Admin.DASHBOARD;
        final int POSITION = 5;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_DatabaseReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Database Reports";
        final String EXPECTED_LINK = Urls.Admin.DATABASE_REPORTS;
        final int POSITION = 6;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_EnergyCompanyAdministration_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Energy Company Administration";
        final String EXPECTED_LINK = Urls.Admin.ENERGY_COMPANY_LIST;
        final int POSITION = 7;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_JobStatus_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Job Status";
        final String EXPECTED_LINK = Urls.Admin.JOB_STATUS;
        final int POSITION = 8;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_Maintenance_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Maintenance";
        final String EXPECTED_LINK = Urls.Admin.MAINTENANCE;
        final int POSITION = 9;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_MultiSpeakSetup_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "MultiSpeak Setup";
        final String EXPECTED_LINK = Urls.Admin.MULTI_SPEAK;
        final int POSITION = 10;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_StatisticalReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Statistical Reports";
        final String EXPECTED_LINK = Urls.Admin.STATISTICAL_REPORTS;
        final int POSITION = 11;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_SubstationsReports_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Substations";
        final String EXPECTED_LINK = Urls.Admin.SUBSTATIONS;
        final int POSITION = 12;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_Surveys_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Surveys";
        final String EXPECTED_LINK = Urls.Admin.SURVEYS;
        final int POSITION = 13;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_Themes_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Themes";
        final String EXPECTED_LINK = Urls.Admin.THEMES;
        final int POSITION = 14;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_UsersAndGroups_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Users and Groups";
        final String EXPECTED_LINK = Urls.Admin.USERS_AND_GROUPS;
        final int POSITION = 15;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void siteMapAdminDetails_YukonConfigSettings_LinkCorrect() {
        softly = new SoftAssertions();
        final String EXPECTED_ANCHOR = "Yukon Configuration Settings";
        final String EXPECTED_LINK = Urls.Admin.CONFIGURATION;
        final int POSITION = 16;

        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}