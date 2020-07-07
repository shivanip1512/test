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

public class SiteMapAdminDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.SITE_MAP);

        siteMapPage = new SiteMapPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }                
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageHasAdminSection() {
        Section adminSection = siteMapPage.getAdminSection();

        assertThat(adminSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageAdminSectionItemCountCorrect() {
        final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageActiveJobsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Active Jobs";
        final String EXPECTED_LINK = Urls.Admin.ACTIVE_JOBS;
        final int POSITION = 0;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageAdministratorReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Administrator Reports";
        final String EXPECTED_LINK = Urls.Admin.REPORTS;
        final int POSITION = 1;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageAllJobsLinkCorrect() {
        final String EXPECTED_ANCHOR = "All Jobs";
        final String EXPECTED_LINK = Urls.Admin.ALL_JOBS;
        final int POSITION = 2;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageCIReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "C&I Reports";
        final String EXPECTED_LINK = Urls.Admin.CI_REPORTS;
        final int POSITION = 3;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageDashboardAdministrationLinkCorrect() {
        final String EXPECTED_ANCHOR = "Dashboard Administration";
        final String EXPECTED_LINK = Urls.Admin.DASHBOARD;
        final int POSITION = 4;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageDatabaseReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Database Reports";
        final String EXPECTED_LINK = Urls.Admin.DATABASE_REPORTS;
        final int POSITION = 5;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageEnergyCompanyAdministrationLinkCorrect() {
        final String EXPECTED_ANCHOR = "Energy Company Administration";
        final String EXPECTED_LINK = Urls.Admin.ENERGY_COMPANY_LIST;
        final int POSITION = 6;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageJobStatusLinkCorrect() {
        final String EXPECTED_ANCHOR = "Job Status";
        final String EXPECTED_LINK = Urls.Admin.JOB_STATUS;
        final int POSITION = 7;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageMaintenanceLinkCorrect() {
        final String EXPECTED_ANCHOR = "Maintenance";
        final String EXPECTED_LINK = Urls.Admin.MAINTENANCE;
        final int POSITION = 8;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageMultiSpeakSetupLinkCorrect() {
        final String EXPECTED_ANCHOR = "MultiSpeak Setup";
        final String EXPECTED_LINK = Urls.Admin.MULTI_SPEAK;
        final int POSITION = 9;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageStatisticalReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Statistical Reports";
        final String EXPECTED_LINK = Urls.Admin.STATISTICAL_REPORTS;
        final int POSITION = 10;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageSubstationsReportsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Substations";
        final String EXPECTED_LINK = Urls.Admin.SUBSTATIONS;
        final int POSITION = 11;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageSurveysLinkCorrect() {
        final String EXPECTED_ANCHOR = "Surveys";
        final String EXPECTED_LINK = Urls.Admin.SURVEYS;
        final int POSITION = 12;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageThemesLinkCorrect() {
        final String EXPECTED_ANCHOR = "Themes";
        final String EXPECTED_LINK = Urls.Admin.THEMES;
        final int POSITION = 13;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageUsersAndGroupsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Users and Groups";
        final String EXPECTED_LINK = Urls.Admin.USERS_AND_GROUPS;
        final int POSITION = 14;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void siteMapDetails_pageYukonConfigSettingsLinkCorrect() {
        final String EXPECTED_ANCHOR = "Yukon Configuration Settings";
        final String EXPECTED_LINK = Urls.Admin.CONFIGURATION;
        final int POSITION = 15;
        
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}