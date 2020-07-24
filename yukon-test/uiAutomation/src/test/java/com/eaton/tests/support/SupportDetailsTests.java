package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SupportPage;

public class SupportDetailsTests extends SeleniumTestSetup {

	private SoftAssertions softly;
	private DriverExtensions driverExt;
    private SupportPage supportPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.SUPPORT);
        softly = new SoftAssertions();
        
        supportPage = new SupportPage(driverExt);
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        

    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN })
    public void supportDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Support";

        String actualPageTitle = supportPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    //================================================================================
    // Support Pages Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageSupportSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 15;

        assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageBatteryNodeAnalysisLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Battery Node Analysis";
        final String EXPECTED_LINK = Urls.Support.BATTERY_NODE_ANALYSIS;
        final int POSITION = 0;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageDataStreamingDeviceAttributesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Data Streaming Device Attributes";
        final String EXPECTED_LINK = Urls.Support.DATA_STREAMING_DEVICE_ATTRIBUTES;
        final int POSITION = 1;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageDatabaseMigrationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Migration";
        final String EXPECTED_LINK = Urls.Support.DATABASE_MIGRATION;
        final int POSITION = 2;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageDeviceDefinitionsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Device Definitions";
        final String EXPECTED_LINK = Urls.Support.DEVICE_DEFINITIONS;
        final int POSITION = 3;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageErrorCodesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Error Codes";
        final String EXPECTED_LINK = Urls.Support.ERROR_CODES;
        final int POSITION = 4;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageEventLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Event Log";
        final String EXPECTED_LINK = Urls.Support.EVENT_LOG;
        final int POSITION = 5;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageFileExportHistoryLinkCorrect() {
    	final String EXPECTED_ANCHOR = "File Export History";
        final String EXPECTED_LINK = Urls.Support.FILE_EXPORT_HISTORY;
        final int POSITION = 6;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageLocalizationHelperLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Localization Helper";
        final String EXPECTED_LINK = Urls.Support.LOCALIZATION_HELPER;
        final int POSITION = 7;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageLogExplorerLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Log Explorer";
        final String EXPECTED_LINK = Urls.Support.LOG_EXPLORER;
        final int POSITION = 8;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageManageIndexesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Manage Indexes";
        final String EXPECTED_LINK = Urls.Support.MANAGE_INDEXES;
        final int POSITION = 9;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageRouteUsageLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Route Usage";
        final String EXPECTED_LINK = Urls.Support.ROUTE_USAGE;
        final int POSITION = 10;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageSystemHealthLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Health";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_HEALTH;
        final int POSITION = 11;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageSystemInfoLinkCorrect() {
    	final String EXPECTED_ANCHOR = "System Info";
        final String EXPECTED_LINK = Urls.Support.SYSTEM_INFO;
        final int POSITION = 12;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageThirdPartyLibrariesLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Third Party Libraries";
        final String EXPECTED_LINK = Urls.Support.THIRD_PARTY_LIBRARIES;
        final int POSITION = 13;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageThreadDumpLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Thread Dump";
        final String EXPECTED_LINK = Urls.Support.THREAD_DUMP;
        final int POSITION = 14;
    	
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getSupportSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    //================================================================================
    // Manuals Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageManualsSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 1;

        assertThat(supportPage.getManualsSectionSimpleList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageYukonManualsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Yukon Manuals (Portal)";
        final String EXPECTED_LINK = Urls.Manuals.YUKON_MANUALS;
        final int POSITION = 0;
    	
        softly.assertThat(supportPage.getManualsSectionSimpleList().getSimpleListItemAnchorTextAt(POSITION)).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getManualsSectionSimpleList().getSimpleListItemLinkTextAt(POSITION)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    //================================================================================
    // Today's Logs
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageTodaysLogsSectionItemCountCorrect() {
    	final int EXPECTED_COUNT = 13;

        assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItems().size()).isGreaterThanOrEqualTo(EXPECTED_COUNT);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageCalcLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Calc";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.CALC_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
    	
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageCapcontrolLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Capcontrol";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.CAPCONTROL_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageDispatchLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Dispatch";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.DISPATCH_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageFDRLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Fdr";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.FDR_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageLoadManagementLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Loadmanagement";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.LOADMANGEMENT_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageMACSLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Macs";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.MACS_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageMessageBrokerLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "MessageBroker";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.MESSAGE_BROKER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageNotificationServerLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "NotificationServer";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.NOTIFICATION_SERVER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pagePorterLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Porter";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.PORTER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageScannerLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Scanner";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.SCANNER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageServiceManagerLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "ServiceManager";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.SERVICE_MANAGER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageWatchdogLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Watchdog";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.WATCHDOG_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageWebserverLogLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Webserver";
        final String EXPECTED_LINK = Urls.Support.LOGGING_VIEW + Urls.Support.WEBSERVER_LOG + supportPage.getCurrentLogTimeStamp() + ".log";
        
        int position = supportPage.getTodaysLogsSectionSimpleList().findSimpleListItemText(EXPECTED_ANCHOR);
        softly.assertThat(position).isNotEqualTo(-1);
        softly.assertThat(supportPage.getTodaysLogsSectionSimpleList().getSimpleListItemLinkTextAt(position)).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageTodaysLogsViewAllLogsLinkCorrect() {
    	final String EXPECTED_ANCHOR = "View All Logs";
        final String EXPECTED_LINK = Urls.Support.LOGGING_MENU + Urls.Support.VIEW_ALL_LOGS;
    	
        softly.assertThat(supportPage.getTodaysLogsViewAllLogsAnchorText()).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getTodaysLogsViewAllLogsLinkText()).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
    
    //================================================================================
    // Database Info
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void pageDatabaseValidationLinkCorrect() {
    	final String EXPECTED_ANCHOR = "Database Validation";
        final String EXPECTED_LINK = Urls.Support.DATABASE_VALIDATION;
    	
        softly.assertThat(supportPage.getDatabaseValidationAnchorText()).isEqualTo(EXPECTED_ANCHOR);
        softly.assertThat(supportPage.getDatabaseValidationLinkText()).isEqualTo(EXPECTED_LINK);
        softly.assertAll();
    }
}
