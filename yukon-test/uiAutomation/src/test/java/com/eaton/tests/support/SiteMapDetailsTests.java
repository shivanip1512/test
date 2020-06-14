package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.SimpleList;
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
    
    private void checkSimpleListLink(SimpleList simpleList, int position, String expectedAnchor, String expectedLink)
    {
    	assertThat(simpleList.getSimpleListItemAnchorTextAt(position)).isEqualTo(expectedAnchor);
        //If the user doesn't have the correct permissions, the link is not included in the HTML. The link therefore won't be checked. 
    	if(simpleList.getSimpleListItemLinkEnabledAt(position)) {
        	assertThat(simpleList.getSimpleListItemLinkTextAt(position)).isEqualTo(expectedLink);
        }
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
        final String EXPECTED_LINK = "/meter/start";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageArchiveDataAnalysisLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Archive Data Analysis";
    	final String EXPECTED_LINK = "/bulk/archiveDataAnalysis/list/view";
    	final int POSITION = 1;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBillingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Billing";
    	final String EXPECTED_LINK = "/billing/home";
    	final int POSITION = 2;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBillingSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Billing Schedules";
    	final String EXPECTED_LINK = "/billing/schedules";
    	final int POSITION = 3;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageBulkImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Import";
    	final String EXPECTED_LINK = "/bulk/import/upload";
    	final int POSITION = 4;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMIBulkUpdateLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Update";
    	final String EXPECTED_LINK = "/bulk/update/upload";
    	final int POSITION = 5;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageManageDashboardsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Manage Dashboards";
    	final String EXPECTED_LINK = "/dashboards/manage";
    	final int POSITION = 6;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterEventsReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Meter Events Report";
    	final String EXPECTED_LINK = "/amr/meterEventsReport/selectDevices";
    	final int POSITION = 7;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterProgrammingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Meter Programming";
    	final String EXPECTED_LINK = "/amr/meterProgramming/home";
    	final int POSITION = 8;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMeterReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Metering Reports";
    	final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=METERING";
    	final int POSITION = 9;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pagePhaseDetectionLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Phase Detection";
    	final String EXPECTED_LINK = "/amr/phaseDetect/home";
    	final int POSITION = 10;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAMIPointImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Point Import";
    	final String EXPECTED_LINK = "/bulk/pointImport/upload";
    	final int POSITION = 11;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageReviewFlaggedPointsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Review Flagged Points";
    	final String EXPECTED_LINK = "/amr/veeReview/home";
    	final int POSITION = 12;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageTrendsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Trends";
    	final String EXPECTED_LINK = "/tools/trends";
    	final int POSITION = 13;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageUsageThresholdReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Usage Threshold Report";
    	final String EXPECTED_LINK = "/amr/usageThresholdReport/report";
    	final int POSITION = 14;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWaterLeakReportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Water Leak Report";
    	final String EXPECTED_LINK = "/amr/waterLeakReport/report";
    	final int POSITION = 15;
        
    	checkSimpleListLink(siteMapPage.getAMISectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/dr/controlArea/list?state=active";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveLoadGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Load Groups";
        final String EXPECTED_LINK = "/dr/loadGroup/list?state=active";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageActiveProgramsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Active Programs";
        final String EXPECTED_LINK = "/dr/program/list?state=ACTIVE";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDRBulkUpdateLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Bulk Update";
        final String EXPECTED_LINK = "/bulk/update/upload";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCICurtailmentLinkCorrect() {
    	final String EXPECTED_ANCHOR = "C&I Curtailment";
        final String EXPECTED_LINK = "/dr/cc/home";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageControlAreasLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Control Areas";
        final String EXPECTED_LINK = "/dr/controlArea/list";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDRDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "DR Dashboard";
        final String EXPECTED_LINK = "/dr/home";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLoadGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Load Groups";
        final String EXPECTED_LINK = "/dr/loadGroup/list";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLoadManagementReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Load Management Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOddsForControlLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Odds for Control";
        final String EXPECTED_LINK = "/operator/Consumer/Odds.jsp";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageProgramsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Programs";
        final String EXPECTED_LINK = "/dr/program/list";
        final int POSITION = 10;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageScenariosLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Scenarios";
        final String EXPECTED_LINK = "/dr/scenario/list";
        final int POSITION = 11;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSetupLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Setup";
        final String EXPECTED_LINK = "/dr/setup/list";
        final int POSITION = 12;
    	
        checkSimpleListLink(siteMapPage.getDRSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/capcontrol/import/view";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCapControlReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "CapControl Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=CAP_CONTROL";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDMVTestLinkCorrect() {
    	final String EXPECTED_ANCHOR = "DMV Test";
        final String EXPECTED_LINK = "/capcontrol/dmvTestList";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageFDRTranslationManagementLinkCorrect() {
    	final String EXPECTED_ANCHOR = "FDR Translation Management";
        final String EXPECTED_LINK = "/bulk/fdrTranslationManager/home";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOrphansLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Orphans";
        final String EXPECTED_LINK = "/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCCPointImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Point Import";
        final String EXPECTED_LINK = "/bulk/pointImport/upload";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRecentTempCapBankMovesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Recent Temp Cap Bank Moves";
        final String EXPECTED_LINK = "/capcontrol/move/movedCapBanks";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCCSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = "/capcontrol/schedules";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageStrategiesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Strategies";
        final String EXPECTED_LINK = "/capcontrol/strategies";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageVoltVarDashboardLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Volt/Var Dashboard";
        final String EXPECTED_LINK = "/capcontrol/tier/areas";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getCCSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/stars/operator/inventory/home";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageComprehensiveMapLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Comprehensive Map";
        final String EXPECTED_LINK = "/stars/comprehensiveMap/home";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCreateAccountLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Create Account";
        final String EXPECTED_LINK = "/stars/operator/account/accountCreate";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageGatewaysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Gateways";
        final String EXPECTED_LINK = "/stars/gateways";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageImportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Import";
        final String EXPECTED_LINK = "/stars/operator/account/accountImport";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOptOutStatusLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Opt Out Status";
        final String EXPECTED_LINK = "/stars/operator/optOut/admin";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageOptOutSurveysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Opt Out Surveys";
        final String EXPECTED_LINK = "/stars/optOutSurvey/list";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pagePurchasingLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Purchasing";
        final String EXPECTED_LINK = "/operator/Hardware/PurchaseTrack.jsp";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRelaysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Relays";
        final String EXPECTED_LINK = "/stars/relay";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRTUsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "RTUs";
        final String EXPECTED_LINK = "/stars/rtu-list";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageServiceOrderListLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Service Order List";
        final String EXPECTED_LINK = "/operator/WorkOrder/WOFilter.jsp";
        final int POSITION = 10;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSTARSReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "STARS Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=STARS";
        final int POSITION = 11;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageViewBatchCommandsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "View Batch Commands";
        final String EXPECTED_LINK = "/operator/Admin/SwitchCommands.jsp";
        final int POSITION = 12;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWorkOrderReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Work Order Reports";
        final String EXPECTED_LINK = "/operator/WorkOrder/Report.jsp";
        final int POSITION = 13;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageWorkOrdersLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Work Orders";
        final String EXPECTED_LINK = "/operator/WorkOrder/WorkOrder.jsp";
        final int POSITION = 14;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageZigBeeProblemDevicesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "ZigBee Problem Devices";
        final String EXPECTED_LINK = "/stars/operator/inventory/zbProblemDevices/view";
        final int POSITION = 15;
    	
        checkSimpleListLink(siteMapPage.getAssetsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/collectionActions/home";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCommanderLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Commander";
        final String EXPECTED_LINK = "/tools/commander";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCreateScheduleLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Create Schedule";
        final String EXPECTED_LINK = "/group/scheduledGroupRequestExecution/home";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDataExportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Data Export";
        final String EXPECTED_LINK = "/tools/data-exporter/view";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDataViewerLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Data Viewer";
        final String EXPECTED_LINK = "/tools/data-viewer";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceConfigurationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Configuration";
        final String EXPECTED_LINK = "/deviceConfiguration/home";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceConfigurationSummaryLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Configuration Summary";
        final String EXPECTED_LINK = "/deviceConfiguration/summary/view";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceGroupUploadLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Group Upload";
        final String EXPECTED_LINK = "/group/updater/upload";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Groups";
        final String EXPECTED_LINK = "/group/editor/home";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageNotesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Notes";
        final String EXPECTED_LINK = "/tools/paoNotes/search";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRecentResultsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Recent Results";
        final String EXPECTED_LINK = "/collectionActions/recentResults";
        final int POSITION = 10;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSchedulesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Schedules";
        final String EXPECTED_LINK = "/group/scheduledGroupRequestExecutionResults/jobs";
        final int POSITION = 11;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageScriptsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Scripts";
        final String EXPECTED_LINK = "/macsscheduler/schedules/view";
        final int POSITION = 12;
    	
        checkSimpleListLink(siteMapPage.getToolsSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/admin/jobsscheduler/active";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAdministratorReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Administrator Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=ADMINISTRATIVE";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageAllJobsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "All Jobs";
        final String EXPECTED_LINK = "/admin/jobsscheduler/all";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageCIReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "C&I Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=CCURT";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDashboardAdministrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Dashboard Administration";
        final String EXPECTED_LINK = "dashboards/admin";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDatabaseReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=DATABASE";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageEnergyCompanyAdministrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Energy Company Administration";
        final String EXPECTED_LINK = "/admin/energyCompany/home";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageJobStatusLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Job Status";
        final String EXPECTED_LINK = "/admin/jobsscheduler/status";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMaintenanceLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Maintenance";
        final String EXPECTED_LINK = "/admin/maintenance/view";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageMultiSpeakSetupLinkCorrect() {
    	final String EXPECTED_ANCHOR = "MultiSpeak Setup";
        final String EXPECTED_LINK = "/multispeak/setup/home";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageStatisticalReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Statistical Reports";
        final String EXPECTED_LINK = "/analysis/Reports.jsp?groupType=STATISTICAL";
        final int POSITION = 10;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSubstationsReportsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Substations";
        final String EXPECTED_LINK = "/admin/substations/routeMapping/view";
        final int POSITION = 11;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSurveysLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Surveys";
        final String EXPECTED_LINK = "/stars/survey/list";
        final int POSITION = 12;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThemesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Themes";
        final String EXPECTED_LINK = "/admin/config/themes";
        final int POSITION = 13;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageUsersAndGroupsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Users and Groups";
        final String EXPECTED_LINK = "/admin/users-groups/home";
        final int POSITION = 14;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageYukonConfigSettingsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Yukon Configuration Settings";
        final String EXPECTED_LINK = "/admin/config/view";
        final int POSITION = 15;
    	
        checkSimpleListLink(siteMapPage.getAdminSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
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
        final String EXPECTED_LINK = "/support/database/migration/home";
        final int POSITION = 0;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDatabaseValidationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Validation";
        final String EXPECTED_LINK = "/support/database/validate/home";
        final int POSITION = 1;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageDeviceDefinitionsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Definitions";
        final String EXPECTED_LINK = "/common/deviceDefinition.xml";
        final int POSITION = 2;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageErrorCodesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Error Codes";
        final String EXPECTED_LINK = "/support/errorCodes/view";
        final int POSITION = 3;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageEventLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Event Log";
        final String EXPECTED_LINK = "/common/eventLog/viewByCategory";
        final int POSITION = 4;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageFileExportHistoryLinkCorrect() {
    	final String EXPECTED_ANCHOR = "File Export History";
        final String EXPECTED_LINK = "/support/fileExportHistory/list";
        final int POSITION = 5;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLocalizationHelperLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Localization Helper";
        final String EXPECTED_LINK = "/support/localization/view";
        final int POSITION = 6;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageLogExplorerLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Log Explorer";
        final String EXPECTED_LINK = "/support/logging/menu";
        final int POSITION = 7;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageManageIndexesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Manage Indexes";
        final String EXPECTED_LINK = "/index/manage";
        final int POSITION = 8;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageRouteUsageLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Route Usage";
        final String EXPECTED_LINK = "/support/routeUsage";
        final int POSITION = 9;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSupportLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Support";
        final String EXPECTED_LINK = "/support";
        final int POSITION = 10;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSystemHealthLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Health";
        final String EXPECTED_LINK = "/support/systemHealth/home";
        final int POSITION = 11;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageSystemInfoLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Info";
        final String EXPECTED_LINK = "/support/info";
        final int POSITION = 12;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThirdPartyLibrariesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Third Party Libraries";
        final String EXPECTED_LINK = "/support/thirdParty/view";
        final int POSITION = 13;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS})
    public void pageThreadDumpLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Thread Dump";
        final String EXPECTED_LINK = "/support/threadDump";
        final int POSITION = 14;
    	
        checkSimpleListLink(siteMapPage.getSupportSectionSimpleList(),POSITION,EXPECTED_ANCHOR,EXPECTED_LINK);
    }
}
