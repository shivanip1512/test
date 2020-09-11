package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.SkipException;
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
    private List<String> supportPageTitles = new ArrayList<>();
    private List<String> supportPageLinks = new ArrayList<>();
    private List<String> manualsPageTitles = new ArrayList<>();
    private List<String> manualsPageLinks = new ArrayList<>();
    private List<String> todaysLogsPageTitles = new ArrayList<>();
    private List<String> todaysLogsPageLinks = new ArrayList<>();
    private String baseUrl;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.SUPPORT);        

        supportPage = new SupportPage(driverExt);

        supportPageTitles = supportPage.getSupportSectionPageList().getListOfItemsText();
        supportPageLinks = supportPage.getSupportSectionPageList().getListOfItemLinks();

        manualsPageTitles = supportPage.getManualsSectionPageList().getListOfItemsText();
        manualsPageLinks = supportPage.getManualsSectionPageList().getListOfItemLinks();

        todaysLogsPageTitles = supportPage.getTodaysLogsSectionPageList().getListOfItemsText();
        todaysLogsPageLinks = supportPage.getTodaysLogsSectionPageList().getListOfItemLinks();

        baseUrl = getBaseUrl();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN })
    public void supportDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Support";

        String actualPageTitle = supportPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    // ================================================================================
    // Support Pages Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_PageCountCorrect() {
        throw new SkipException("Related to Development Defect: YUK-22612");
//        final int EXPECTED_COUNT = 15;
//        assertThat(supportPage.getSupportSectionPageList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_BatteryNodeAnalysis_DisplayedAndCorrectLink() {

        throw new SkipException("Development Defect created: YUK-22612");
//        softly = new SoftAssertions();
//        softly.assertThat(supportPageTitles).contains("Battery Node Analysis");
//        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.BATTERY_NODE_ANALYSIS);
//        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_DataStreamingDeviceAttributes_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Data Streaming Device Attributes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DATA_STREAMING_DEVICE_ATTRIBUTES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_DatabaseMigration_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Database Migration");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DATABASE_MIGRATION);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_DeviceDefinitions_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Device Definitions");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DEVICE_DEFINITIONS);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_ErrorCodes_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Error Codes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.ERROR_CODES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_EventLog_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Event Log");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.EVENT_LOG);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_FileExportHistory_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("File Export History");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.FILE_EXPORT_HISTORY);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_LocalizationHelper_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Localization Helper");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.LOCALIZATION_HELPER);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_LogExplorer_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Log Explorer");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.LOG_EXPLORER);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_ManageIndexes_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Manage Indexes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.MANAGE_INDEXES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_RouteUsage_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Route Usage");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.ROUTE_USAGE);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_SystemHealth_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("System Health");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.SYSTEM_HEALTH);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_SystemInfo_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("System Info");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.SYSTEM_INFO);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_ThirdPartyLibraries_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Third Party Libraries");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.THIRD_PARTY_LIBRARIES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_SupportSection_ThreadDump_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Thread Dump");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.THREAD_DUMP);
        softly.assertAll();
    }

    // ================================================================================
    // Manuals Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_ManualsSection_PageCountCorrect() {
        final int EXPECTED_COUNT = 1;
        assertThat(supportPage.getManualsSectionPageList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_ManualsSection_YukonManuals_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(manualsPageTitles).contains("Yukon Manuals");
        softly.assertThat(manualsPageLinks).contains(Urls.Manuals.YUKON_MANUALS);
        softly.assertAll();
    }

    // ================================================================================
    // Today's Logs
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_PageCountCorrect() {
        final int EXPECTED_COUNT = 13;
        assertThat(supportPage.getTodaysLogsSectionPageList().getSimpleListItems().size()).isGreaterThanOrEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Calc_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Calc");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.CALC_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Capcontrol_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Capcontrol");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.CAPCONTROL_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Dispatch_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Dispatch");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.DISPATCH_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Fdr_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Fdr");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.FDR_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_LoadManagement_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Loadmanagement");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.LOADMANGEMENT_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Macs_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Macs");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.MACS_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_MessageBroker_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("MessageBroker");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.MESSAGE_BROKER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_NotificationServer_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("NotificationServer");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.NOTIFICATION_SERVER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Porter_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Porter");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.PORTER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Scanner_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Scanner");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.SCANNER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_ServiceManager_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("ServiceManager");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.SERVICE_MANAGER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Watchdog_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Watchdog");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.WATCHDOG_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_Webserver_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Webserver");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.WEBSERVER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_TodaysLogsSection_ViewAllLogs_DisplayedAndCorrectLink() {
        softly = new SoftAssertions();

        softly.assertThat(supportPage.getViewAllLogsText()).isEqualTo("View All Logs");
        softly.assertThat(supportPage.getViewAllLogsLink())
                .isEqualTo(baseUrl + Urls.Support.LOGGING_MENU + Urls.Support.VIEW_ALL_LOGS);
        softly.assertAll();
    }

    // ================================================================================
    // Database Info
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void supportPage_DatabaseInfo_DatabaseValidation_DisplayedAndCorrectLink() {
        softly.assertThat(supportPage.getDatabaseValidationText()).isEqualTo("Database Validation");
        softly.assertThat(supportPage.getDatabaseValidationLink()).isEqualTo(baseUrl + Urls.Support.DATABASE_VALIDATION);
        softly.assertAll();
    }
}
