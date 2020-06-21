package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SiteMapPage;

public class SiteMapDetailsTests extends SeleniumTestSetup {

    private SiteMapPage siteMapPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.SITE_MAP);

        siteMapPage = new SiteMapPage(driverExt);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Site Map";

        String actualPageTitle = siteMapPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    //================================================================================
    // AMI Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasAMISection() {
        Section amiSection = siteMapPage.getAMISection();

        assertThat(amiSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMISectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMIDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "AMI Dashboard";
        final String EXPECTED_LINK = Urls.Ami.DASHBOARD;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageArchiveDataAnalysisLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Archive Data Analysis";
    	final String EXPECTED_LINK = Urls.Ami.ARCHIVE_DATA_ANALYSIS;
    	final int POSITION = 1;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBillingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Billing";
    	final String EXPECTED_LINK = Urls.Ami.BILLING;
    	final int POSITION = 2;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBillingSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Billing Schedules";
    	final String EXPECTED_LINK = Urls.Ami.BILLING_SCHEDULES;
    	final int POSITION = 3;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBulkImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Import";
    	final String EXPECTED_LINK = Urls.Ami.BULK_IMPORT;
    	final int POSITION = 4;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMIBulkUpdateLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Update";
    	final String EXPECTED_LINK = Urls.Ami.BULK_UPDATE;
    	final int POSITION = 5;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageManageDashboardsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Manage Dashboards";
    	final String EXPECTED_LINK = Urls.Ami.MANAGE_DASHBOARDS;
    	final int POSITION = 6;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterEventsReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Meter Events Report";
    	final String EXPECTED_LINK = Urls.Ami.METER_EVENTS_REPORTS;
    	final int POSITION = 7;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterProgrammingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Meter Programming";
    	final String EXPECTED_LINK = Urls.Ami.METER_PROGRAMMING;
    	final int POSITION = 8;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Metering Reports";
    	final String EXPECTED_LINK = Urls.Ami.REPORTS;
    	final int POSITION = 9;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pagePhaseDetectionLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Phase Detection";
    	final String EXPECTED_LINK = Urls.Ami.PHASE_DETECTION;
    	final int POSITION = 10;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMIPointImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Point Import";
    	final String EXPECTED_LINK = Urls.Ami.POINT_IMPORT;
    	final int POSITION = 11;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageReviewFlaggedPointsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Review Flagged Points";
    	final String EXPECTED_LINK = Urls.Ami.REVIEW_FLAGGED_POINTS;
    	final int POSITION = 12;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageTrendsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Trends";
    	final String EXPECTED_LINK = Urls.Tools.TRENDS;
    	final int POSITION = 13;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageUsageThresholdReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Usage Threshold Report";
    	final String EXPECTED_LINK = Urls.Ami.USAGE_THRESHOLD_REPORT;
    	final int POSITION = 14;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWaterLeakReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Water Leak Report";
    	final String EXPECTED_LINK = Urls.Ami.WATER_LEAK_REPORT;
    	final int POSITION = 15;
        
    	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAMISectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Demand Response Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasDRSection() {
        Section drSection = siteMapPage.getDRSection();

        assertThat(drSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDRSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 13;

    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveControlAreasLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Control Areas";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_CONTROL_AREAS;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveLoadGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Load Groups";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_LOAD_GROUPS;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveProgramsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Programs";
        final String EXPECTED_LINK = Urls.DemandResponse.ACTIVE_PROGRAMS;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDRBulkUpdateLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Update";
        final String EXPECTED_LINK = Urls.DemandResponse.BULK_UPDATE;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCICurtailmentLinkCorrect() {
    	final String EXPECTED_ANCHOR = "C&I Curtailment";
        final String EXPECTED_LINK = Urls.DemandResponse.CI_CURTAILMENT;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageControlAreasLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Control Areas";
        final String EXPECTED_LINK = Urls.DemandResponse.CONTROL_AREA;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDRDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "DR Dashboard";
        final String EXPECTED_LINK = Urls.DemandResponse.DASHBOARD;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLoadGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Load Groups";
        final String EXPECTED_LINK = Urls.DemandResponse.LOAD_GROUPS;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLoadManagementReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Load Management Reports";
        final String EXPECTED_LINK = Urls.DemandResponse.REPORTS;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOddsForControlLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Odds for Control";
        final String EXPECTED_LINK = Urls.DemandResponse.ODDS_FOR_CONTROL;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageProgramsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Programs";
        final String EXPECTED_LINK = Urls.DemandResponse.PROGRAMS;
        final int POSITION = 10;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageScenariosLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Scenarios";
        final String EXPECTED_LINK = Urls.DemandResponse.SCENARIOS;
        final int POSITION = 11;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSetupLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Setup";
        final String EXPECTED_LINK = Urls.DemandResponse.SETUP;
        final int POSITION = 12;
    	
        assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getDRSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Volt/Var Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasCCSection() {
        Section ccSection = siteMapPage.getCCSection();

        assertThat(ccSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCCSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 10;

        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCapControlImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "CapControl Import";
        final String EXPECTED_LINK = Urls.CapControl.IMPORT;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCapControlReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "CapControl Reports";
        final String EXPECTED_LINK = Urls.CapControl.REPORTS;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDMVTestLinkCorrect() {
    	final String EXPECTED_ANCHOR = "DMV Test";
        final String EXPECTED_LINK = Urls.CapControl.DMV_TEST_LIST;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageFDRTranslationManagementLinkCorrect() {
    	final String EXPECTED_ANCHOR = "FDR Translation Management";
        final String EXPECTED_LINK = Urls.CapControl.FDR_TRANSLATION_MANAGER;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOrphansLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Orphans";
        final String EXPECTED_LINK = Urls.CapControl.ORPHANS;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCCPointImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Point Import";
        final String EXPECTED_LINK = Urls.CapControl.POINT_IMPORT;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRecentTempCapBankMovesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Recent Temp Cap Bank Moves";
        final String EXPECTED_LINK = Urls.CapControl.RECENT_TEMP_MOVES;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCCSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = Urls.CapControl.SCHEDULES;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageStrategiesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Strategies";
        final String EXPECTED_LINK = Urls.CapControl.STRATEGIES;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageVoltVarDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Volt/Var Dashboard";
        final String EXPECTED_LINK = Urls.CapControl.DASHBOARD;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getCCSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Assets Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasAssetsSection() {
        Section assetsSection = siteMapPage.getAssetsSection();

        assertThat(assetsSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAssetsSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAssetsDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Assets Dashboard";
        final String EXPECTED_LINK = Urls.Assets.DASHBOARD;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageComprehensiveMapLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Comprehensive Map";
        final String EXPECTED_LINK = Urls.Assets.COMPREHENSIVE_MAP;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCreateAccountLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Create Account";
        final String EXPECTED_LINK = Urls.Assets.CREATE_ACCOUNT;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageGatewaysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Gateways";
        final String EXPECTED_LINK = Urls.Assets.GATEWAYS;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Import";
        final String EXPECTED_LINK = Urls.Assets.IMPORT;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOptOutStatusLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Opt Out Status";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_STATUS;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOptOutSurveysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Opt Out Surveys";
        final String EXPECTED_LINK = Urls.Assets.OPT_OUT_SURVEYS;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pagePurchasingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Purchasing";
        final String EXPECTED_LINK = Urls.Assets.PURCHASING;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRelaysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Relays";
        final String EXPECTED_LINK = Urls.Assets.RELAYS;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRTUsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "RTUs";
        final String EXPECTED_LINK = Urls.Assets.RTUS;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageServiceOrderListLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Service Order List";
        final String EXPECTED_LINK = Urls.Assets.SERVICE_ORDER_LIST;
        final int POSITION = 10;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSTARSReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "STARS Reports";
        final String EXPECTED_LINK = Urls.Assets.REPORTS;
        final int POSITION = 11;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageViewBatchCommandsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "View Batch Commands";
        final String EXPECTED_LINK = Urls.Assets.VIEW_BATCH_COMMANDS;
        final int POSITION = 12;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWorkOrderReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Work Order Reports";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDER_REPORTS;
        final int POSITION = 13;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWorkOrdersLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Work Orders";
        final String EXPECTED_LINK = Urls.Assets.WORK_ORDERS;
        final int POSITION = 14;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageZigBeeProblemDevicesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "ZigBee Problem Devices";
        final String EXPECTED_LINK = Urls.Assets.ZIGBEE_PROBLEM_DEVICES;
        final int POSITION = 15;
    	
        assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAssetsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Tools Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasToolsSection() {
        Section toolsSection = siteMapPage.getToolsSection();

        assertThat(toolsSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageToolsSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 13;

        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCollectionActionsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Collection Actions";
        final String EXPECTED_LINK = Urls.Tools.COLLECTION_ACTIONS;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCommanderLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Commander";
        final String EXPECTED_LINK = Urls.Tools.COMMANDER;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCreateScheduleLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Create Schedule";
        final String EXPECTED_LINK = Urls.Tools.CREATE_SCHEDULE;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDataExportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Data Export";
        final String EXPECTED_LINK = Urls.Tools.DATA_EXPORTER;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDataViewerLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Data Viewer";
        final String EXPECTED_LINK = Urls.Tools.DATA_VIEWER;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceConfigurationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Configuration";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceConfigurationSummaryLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Configuration Summary";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_CONFIGURATION_SUMMARY;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceGroupUploadLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Group Upload";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP_UPLOAD;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Groups";
        final String EXPECTED_LINK = Urls.Tools.DEVICE_GROUP;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageNotesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Notes";
        final String EXPECTED_LINK = Urls.Tools.NOTES;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRecentResultsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Recent Results";
        final String EXPECTED_LINK = Urls.Tools.RECENT_RESULTS;
        final int POSITION = 10;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = Urls.Tools.SCHEDULES;
        final int POSITION = 11;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageScriptsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Scripts";
        final String EXPECTED_LINK = Urls.Tools.SCRIPTS;
        final int POSITION = 12;
    	
        assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getToolsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Admin Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasAdminSection() {
        Section adminSection = siteMapPage.getAdminSection();

        assertThat(adminSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAdminSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 16;

        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveJobsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Jobs";
        final String EXPECTED_LINK = Urls.Admin.ACTIVE_JOBS;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAdministratorReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Administrator Reports";
        final String EXPECTED_LINK = Urls.Admin.REPORTS;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAllJobsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "All Jobs";
        final String EXPECTED_LINK = Urls.Admin.ALL_JOBS;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCIReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "C&I Reports";
        final String EXPECTED_LINK = Urls.Admin.CI_REPORTS;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDashboardAdministrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Dashboard Administration";
        final String EXPECTED_LINK = Urls.Admin.DASHBOARD;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDatabaseReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Reports";
        final String EXPECTED_LINK = Urls.Admin.DATABASE_REPORTS;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageEnergyCompanyAdministrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Energy Company Administration";
        final String EXPECTED_LINK = Urls.Admin.ENERGY_COMPANY_LIST;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageJobStatusLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Job Status";
        final String EXPECTED_LINK = Urls.Admin.JOB_STATUS;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMaintenanceLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Maintenance";
        final String EXPECTED_LINK = Urls.Admin.MAINTENANCE;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMultiSpeakSetupLinkCorrect() {
    	final String EXPECTED_ANCHOR = "MultiSpeak Setup";
        final String EXPECTED_LINK = Urls.Admin.MULTI_SPEAK;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageStatisticalReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Statistical Reports";
        final String EXPECTED_LINK = Urls.Admin.STATISTICAL_REPORTS;
        final int POSITION = 10;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSubstationsReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Substations";
        final String EXPECTED_LINK = Urls.Admin.SUBSTATIONS;
        final int POSITION = 11;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSurveysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Surveys";
        final String EXPECTED_LINK = Urls.Admin.SURVEYS;
        final int POSITION = 12;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThemesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Themes";
        final String EXPECTED_LINK = Urls.Admin.THEMES;
        final int POSITION = 13;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageUsersAndGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Users and Groups";
        final String EXPECTED_LINK = Urls.Admin.USERS_AND_GROUPS;
        final int POSITION = 14;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageYukonConfigSettingsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Yukon Configuration Settings";
        final String EXPECTED_LINK = Urls.Admin.CONFIGURATION;
        final int POSITION = 15;
    	
        assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getAdminSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    //================================================================================
    // Support Section
    //================================================================================
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageHasSupportSection() {
        Section supportSection = siteMapPage.getSupportSection();

        assertThat(supportSection).isNotNull();
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSupportSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 15;

        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDatabaseMigrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Migration";
        final String EXPECTED_LINK = Urls.Support.DATABASE_MIGRATION;
        final int POSITION = 0;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDatabaseValidationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Validation";
        final String EXPECTED_LINK = Urls.Support.DATABASE_VALIDATION;
        final int POSITION = 1;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceDefinitionsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Definitions";
        final String EXPECTED_LINK = Urls.Support.DEVICE_DEFINITIONS;
        final int POSITION = 2;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageErrorCodesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Error Codes";
        final String EXPECTED_LINK = Urls.Support.ERROR_CODES;
        final int POSITION = 3;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageEventLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Event Log";
        final String EXPECTED_LINK = Urls.Support.EVENT_LOG;
        final int POSITION = 4;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageFileExportHistoryLinkCorrect() {
    	final String EXPECTED_ANCHOR = "File Export History";
        final String EXPECTED_LINK = Urls.Support.FILE_EXPORT_HISTORY;
        final int POSITION = 5;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLocalizationHelperLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Localization Helper";
        final String EXPECTED_LINK = Urls.Support.LOCALIZATION_HELPER;
        final int POSITION = 6;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLogExplorerLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Log Explorer";
        final String EXPECTED_LINK = Urls.Support.LOG_EXPLORER;
        final int POSITION = 7;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageManageIndexesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Manage Indexes";
        final String EXPECTED_LINK = Urls.Support.MANAGE_INDEXES;
        final int POSITION = 8;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRouteUsageLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Route Usage";
        final String EXPECTED_LINK = Urls.Support.ROUTE_USAGE;
        final int POSITION = 9;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSupportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Support";
        final String EXPECTED_LINK = Urls.SUPPORT;
        final int POSITION = 10;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSystemHealthLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Health";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_HEALTH;
        final int POSITION = 11;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSystemInfoLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Info";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_INFO;
        final int POSITION = 12;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThirdPartyLibrariesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Third Party Libraries";
        final String EXPECTED_LINK = Urls.Support.THIRD_PARTY_LIBRARIES;
        final int POSITION = 13;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThreadDumpLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Thread Dump";
        final String EXPECTED_LINK = Urls.Support.THREAD_DUMP;
        final int POSITION = 14;
    	
        assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkEnabledAt(POSITION)) {
        	assertThat(siteMapPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        }
    }
}
