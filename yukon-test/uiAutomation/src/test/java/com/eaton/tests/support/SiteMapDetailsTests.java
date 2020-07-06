//package com.eaton.tests.support;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.openqa.selenium.WebDriver;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import com.eaton.framework.DriverExtensions;
//import com.eaton.framework.SeleniumTestSetup;
//import com.eaton.framework.TestConstants;
//import com.eaton.framework.Urls;
//import com.eaton.pages.support.SiteMapPage;
//
//public class SiteMapDetailsTests extends SeleniumTestSetup {
//
//    private SiteMapPage siteMapPage;
//
//    @BeforeClass(alwaysRun=true)
//    public void beforeClass() {
//        WebDriver driver = getDriver();
//        DriverExtensions driverExt = getDriverExt();
//
//        driver.get(getBaseUrl() + Urls.SITE_MAP);
//
//        siteMapPage = new SiteMapPage(driverExt);
//    }
//
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void pageTitleCorrect() {
//        final String EXPECTED_TITLE = "Site Map";
//
//        String actualPageTitle = siteMapPage.getPageTitle();
//
//        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
//    }
//    
//    //================================================================================
//    // AMI Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasAMISection() {
//        Section amiSection = siteMapPage.getAMISection();
//
//        assertThat(amiSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAMISectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 16;
//
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAMIDashboardLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "AMI Dashboard";
//        final String EXPECTED_LINK = Urls.Ami.DASHBOARD;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageArchiveDataAnalysisLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Archive Data Analysis";
//    	final String EXPECTED_LINK = Urls.Ami.ARCHIVE_DATA_ANALYSIS;
//    	final int POSITION = 1;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageBillingLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Billing";
//    	final String EXPECTED_LINK = Urls.Ami.BILLING;
//    	final int POSITION = 2;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageBillingSchedulesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Billing Schedules";
//    	final String EXPECTED_LINK = Urls.Ami.BILLING_SCHEDULES;
//    	final int POSITION = 3;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageBulkImportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Bulk Import";
//    	final String EXPECTED_LINK = Urls.Ami.BULK_IMPORT;
//    	final int POSITION = 4;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAMIBulkUpdateLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Bulk Update";
//    	final String EXPECTED_LINK = Urls.Ami.BULK_UPDATE;
//    	final int POSITION = 5;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageManageDashboardsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Manage Dashboards";
//    	final String EXPECTED_LINK = Urls.Ami.MANAGE_DASHBOARDS;
//    	final int POSITION = 6;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageMeterEventsReportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Meter Events Report";
//    	final String EXPECTED_LINK = Urls.Ami.METER_EVENTS_REPORTS;
//    	final int POSITION = 7;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageMeterProgrammingLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Meter Programming";
//    	final String EXPECTED_LINK = Urls.Ami.METER_PROGRAMMING;
//    	final int POSITION = 8;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageMeterReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Metering Reports";
//    	final String EXPECTED_LINK = Urls.Ami.REPORTS;
//    	final int POSITION = 9;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pagePhaseDetectionLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Phase Detection";
//    	final String EXPECTED_LINK = Urls.Ami.PHASE_DETECTION;
//    	final int POSITION = 10;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAMIPointImportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Point Import";
//    	final String EXPECTED_LINK = Urls.Ami.POINT_IMPORT;
//    	final int POSITION = 11;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageReviewFlaggedPointsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Review Flagged Points";
//    	final String EXPECTED_LINK = Urls.Ami.REVIEW_FLAGGED_POINTS;
//    	final int POSITION = 12;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageTrendsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Trends";
//    	final String EXPECTED_LINK = Urls.Tools.TRENDS;
//    	final int POSITION = 13;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageUsageThresholdReportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Usage Threshold Report";
//    	final String EXPECTED_LINK = Urls.Ami.USAGE_THRESHOLD_REPORT;
//    	final int POSITION = 14;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageWaterLeakReportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Water Leak Report";
//    	final String EXPECTED_LINK = Urls.Ami.WATER_LEAK_REPORT;
//    	final int POSITION = 15;
//        
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Demand Response Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasDRSection() {
//        Section drSection = siteMapPage.getDRSection();
//
//        assertThat(drSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDRSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 13;
//
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageActiveControlAreasLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Active Control Areas";
//        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_CONTROL_AREAS;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageActiveLoadGroupsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Active Load Groups";
//        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_LOAD_GROUPS;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageActiveProgramsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Active Programs";
//        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_PROGRAMS;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDRBulkUpdateLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Bulk Update";
//        final String EXPECTED_LINK = Urls.DemandResponse.BULK_UPDATE;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCICurtailmentLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "C&I Curtailment";
//        final String EXPECTED_LINK = Urls.DemandResponse.CI_CURTAILMENT;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageControlAreasLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Control Areas";
//        final String EXPECTED_LINK = Urls.DemandResponse.CONTROL_AREA;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDRDashboardLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "DR Dashboard";
//        final String EXPECTED_LINK = Urls.DemandResponse.DASHBOARD;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageLoadGroupsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Load Groups";
//        final String EXPECTED_LINK = Urls.DemandResponse.LOAD_GROUPS;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageLoadManagementReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Load Management Reports";
//        final String EXPECTED_LINK = Urls.DemandResponse.REPORTS;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageOddsForControlLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Odds for Control";
//        final String EXPECTED_LINK = Urls.DemandResponse.ODDS_FOR_CONTROL;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageProgramsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Programs";
//        final String EXPECTED_LINK = Urls.DemandResponse.PROGRAMS;
//        final int POSITION = 10;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageScenariosLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Scenarios";
//        final String EXPECTED_LINK = Urls.DemandResponse.SCENARIOS;
//        final int POSITION = 11;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSetupLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Setup";
//        final String EXPECTED_LINK = Urls.DemandResponse.SETUP;
//        final int POSITION = 12;
//    	
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Volt/Var Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasCCSection() {
//        Section ccSection = siteMapPage.getCCSection();
//
//        assertThat(ccSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCCSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 10;
//
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCapControlImportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "CapControl Import";
//        final String EXPECTED_LINK = Urls.CapControl.IMPORT;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCapControlReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "CapControl Reports";
//        final String EXPECTED_LINK = Urls.CapControl.REPORTS;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDMVTestLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "DMV Test";
//        final String EXPECTED_LINK = Urls.CapControl.DMV_TEST_LIST;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageFDRTranslationManagementLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "FDR Translation Management";
//        final String EXPECTED_LINK = Urls.CapControl.FDR_TRANSLATION_MANAGER;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageOrphansLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Orphans";
//        final String EXPECTED_LINK = Urls.CapControl.ORPHANS;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCCPointImportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Point Import";
//        final String EXPECTED_LINK = Urls.CapControl.POINT_IMPORT;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageRecentTempCapBankMovesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Recent Temp Cap Bank Moves";
//        final String EXPECTED_LINK = Urls.CapControl.RECENT_TEMP_MOVES;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCCSchedulesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Schedules";
//        final String EXPECTED_LINK = Urls.CapControl.SCHEDULES;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageStrategiesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Strategies";
//        final String EXPECTED_LINK = Urls.CapControl.STRATEGIES;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageVoltVarDashboardLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Volt/Var Dashboard";
//        final String EXPECTED_LINK = Urls.CapControl.DASHBOARD;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Assets Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasAssetsSection() {
//        Section assetsSection = siteMapPage.getAssetsSection();
//
//        assertThat(assetsSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAssetsSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 18;
//
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAssetsDashboardLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Assets Dashboard";
//        final String EXPECTED_LINK = Urls.Assets.DASHBOARD;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCommChannelsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Comm Channels";
//        final String EXPECTED_LINK = Urls.Assets.COMM_CHANNELS_LIST;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageComprehensiveMapLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Comprehensive Map";
//        final String EXPECTED_LINK = Urls.Assets.COMPREHENSIVE_MAP;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCreateAccountLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Create Account";
//        final String EXPECTED_LINK = Urls.Assets.CREATE_ACCOUNT;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageGatewaysLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Gateways";
//        final String EXPECTED_LINK = Urls.Assets.GATEWAYS;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageImportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Import";
//        final String EXPECTED_LINK = Urls.Assets.IMPORT;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageOptOutStatusLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Opt Out Status";
//        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_STATUS;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageOptOutSurveysLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Opt Out Surveys";
//        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_SURVEYS;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pagePurchasingLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Purchasing";
//        final String EXPECTED_LINK = Urls.Assets.PURCHASING;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageRelaysLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Relays";
//        final String EXPECTED_LINK = Urls.Assets.RELAYS;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageRTUsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "RTUs";
//        final String EXPECTED_LINK = Urls.Assets.RTUS;
//        final int POSITION = 10;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageServiceOrderListLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Service Order List";
//        final String EXPECTED_LINK = Urls.Assets.SERVICE_ORDER_LIST;
//        final int POSITION = 11;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSTARSReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "STARS Reports";
//        final String EXPECTED_LINK = Urls.Assets.REPORTS;
//        final int POSITION = 12;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageViewBatchCommandsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "View Batch Commands";
//        final String EXPECTED_LINK = Urls.Assets.VIEW_BATCH_COMMANDS;
//        final int POSITION = 13;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageVirtualDevicesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Virtual Devices";
//        final String EXPECTED_LINK = Urls.Assets.VIRTUAL_DEVICES;
//        final int POSITION = 14;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageWorkOrderReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Work Order Reports";
//        final String EXPECTED_LINK = Urls.Assets.WORK_ORDER_REPORTS;
//        final int POSITION = 15;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageWorkOrdersLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Work Orders";
//        final String EXPECTED_LINK = Urls.Assets.WORK_ORDERS;
//        final int POSITION = 16;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageZigBeeProblemDevicesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "ZigBee Problem Devices";
//        final String EXPECTED_LINK = Urls.Assets.ZIGBEE_PROBLEM_DEVICES;
//        final int POSITION = 17;
//    	
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Tools Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasToolsSection() {
//        Section toolsSection = siteMapPage.getToolsSection();
//
//        assertThat(toolsSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageToolsSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 14;
//
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCollectionActionsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Collection Actions";
//        final String EXPECTED_LINK = Urls.Tools.COLLECTION_ACTIONS;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCommanderLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Commander";
//        final String EXPECTED_LINK = Urls.Tools.COMMANDER;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCreateScheduleLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Create Schedule";
//        final String EXPECTED_LINK = Urls.Tools.CREATE_SCHEDULE;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDataExportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Data Export";
//        final String EXPECTED_LINK = Urls.Tools.DATA_EXPORTER;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDataStreamingLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Data Streaming";
//        final String EXPECTED_LINK = Urls.Tools.DATA_STREAMING;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDataViewerLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Data Viewer";
//        final String EXPECTED_LINK = Urls.Tools.DATA_VIEWER;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDeviceConfigurationLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Device Configuration";
//        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDeviceConfigurationSummaryLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Device Configuration Summary";
//        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION_SUMMARY;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDeviceGroupUploadLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Device Group Upload";
//        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP_UPLOAD;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDeviceGroupsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Device Groups";
//        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageNotesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Notes";
//        final String EXPECTED_LINK = Urls.Tools.NOTES;
//        final int POSITION = 10;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageRecentResultsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Recent Results";
//        final String EXPECTED_LINK = Urls.Tools.RECENT_RESULTS;
//        final int POSITION = 11;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSchedulesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Schedules";
//        final String EXPECTED_LINK = Urls.Tools.SCHEDULES;
//        final int POSITION = 12;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageScriptsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Scripts";
//        final String EXPECTED_LINK = Urls.Tools.SCRIPTS;
//        final int POSITION = 13;
//    	
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Admin Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasAdminSection() {
//        Section adminSection = siteMapPage.getAdminSection();
//
//        assertThat(adminSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAdminSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 16;
//
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageActiveJobsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Active Jobs";
//        final String EXPECTED_LINK = Urls.Admin.ACTIVE_JOBS;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAdministratorReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Administrator Reports";
//        final String EXPECTED_LINK = Urls.Admin.REPORTS;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageAllJobsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "All Jobs";
//        final String EXPECTED_LINK = Urls.Admin.ALL_JOBS;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageCIReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "C&I Reports";
//        final String EXPECTED_LINK = Urls.Admin.CI_REPORTS;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDashboardAdministrationLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Dashboard Administration";
//        final String EXPECTED_LINK = Urls.Admin.DASHBOARD;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDatabaseReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Database Reports";
//        final String EXPECTED_LINK = Urls.Admin.DATABASE_REPORTS;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageEnergyCompanyAdministrationLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Energy Company Administration";
//        final String EXPECTED_LINK = Urls.Admin.ENERGY_COMPANY_LIST;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageJobStatusLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Job Status";
//        final String EXPECTED_LINK = Urls.Admin.JOB_STATUS;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageMaintenanceLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Maintenance";
//        final String EXPECTED_LINK = Urls.Admin.MAINTENANCE;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageMultiSpeakSetupLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "MultiSpeak Setup";
//        final String EXPECTED_LINK = Urls.Admin.MULTI_SPEAK;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageStatisticalReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Statistical Reports";
//        final String EXPECTED_LINK = Urls.Admin.STATISTICAL_REPORTS;
//        final int POSITION = 10;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSubstationsReportsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Substations";
//        final String EXPECTED_LINK = Urls.Admin.SUBSTATIONS;
//        final int POSITION = 11;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSurveysLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Surveys";
//        final String EXPECTED_LINK = Urls.Admin.SURVEYS;
//        final int POSITION = 12;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageThemesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Themes";
//        final String EXPECTED_LINK = Urls.Admin.THEMES;
//        final int POSITION = 13;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageUsersAndGroupsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Users and Groups";
//        final String EXPECTED_LINK = Urls.Admin.USERS_AND_GROUPS;
//        final int POSITION = 14;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageYukonConfigSettingsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Yukon Configuration Settings";
//        final String EXPECTED_LINK = Urls.Admin.CONFIGURATION;
//        final int POSITION = 15;
//    	
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    //================================================================================
//    // Support Section
//    //================================================================================
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageHasSupportSection() {
//        Section supportSection = siteMapPage.getSupportSection();
//
//        assertThat(supportSection).isNotNull();
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSupportSectionItemCountCorrect() {
//    	final int EXPECTED_COUNT = 16;
//
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDataStreamingDeviceAttributesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Data Streaming Device Attributes";
//        final String EXPECTED_LINK = Urls.Support.DATA_STREAMING_DEVICE_ATTRIBUTES;
//        final int POSITION = 0;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDatabaseMigrationLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Database Migration";
//        final String EXPECTED_LINK = Urls.Support.DATABASE_MIGRATION;
//        final int POSITION = 1;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDatabaseValidationLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Database Validation";
//        final String EXPECTED_LINK = Urls.Support.DATABASE_VALIDATION;
//        final int POSITION = 2;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageDeviceDefinitionsLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Device Definitions";
//        final String EXPECTED_LINK = Urls.Support.DEVICE_DEFINITIONS;
//        final int POSITION = 3;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageErrorCodesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Error Codes";
//        final String EXPECTED_LINK = Urls.Support.ERROR_CODES;
//        final int POSITION = 4;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageEventLogLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Event Log";
//        final String EXPECTED_LINK = Urls.Support.EVENT_LOG;
//        final int POSITION = 5;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageFileExportHistoryLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "File Export History";
//        final String EXPECTED_LINK = Urls.Support.FILE_EXPORT_HISTORY;
//        final int POSITION = 6;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageLocalizationHelperLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Localization Helper";
//        final String EXPECTED_LINK = Urls.Support.LOCALIZATION_HELPER;
//        final int POSITION = 7;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageLogExplorerLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Log Explorer";
//        final String EXPECTED_LINK = Urls.Support.LOG_EXPLORER;
//        final int POSITION = 8;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageManageIndexesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Manage Indexes";
//        final String EXPECTED_LINK = Urls.Support.MANAGE_INDEXES;
//        final int POSITION = 9;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageRouteUsageLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Route Usage";
//        final String EXPECTED_LINK = Urls.Support.ROUTE_USAGE;
//        final int POSITION = 10;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSupportLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Support";
//        final String EXPECTED_LINK = Urls.SUPPORT;
//        final int POSITION = 11;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSystemHealthLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "System Health";
//        final String EXPECTED_LINK = Urls.Support.SYSTEM_HEALTH;
//        final int POSITION = 12;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageSystemInfoLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "System Info";
//        final String EXPECTED_LINK = Urls.Support.SYSTEM_INFO;
//        final int POSITION = 13;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageThirdPartyLibrariesLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Third Party Libraries";
//        final String EXPECTED_LINK = Urls.Support.THIRD_PARTY_LIBRARIES;
//        final int POSITION = 14;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//    
//    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
//    public void siteMapDetails_pageThreadDumpLinkCorrect() {
//    	final String EXPECTED_ANCHOR = "Thread Dump";
//        final String EXPECTED_LINK = Urls.Support.THREAD_DUMP;
//        final int POSITION = 15;
//    	
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
//        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
//    }
//}
