package com.eaton.tests.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN })
    public void supportDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Support";

        String actualPageTitle = supportPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

//    Defect YUK-22612 was closed and they will not fix so these tests will need to be done another way
//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
//    public void supportDetails_SupportPages_CountCorrect() {
//        throw new SkipException("Related to Development Defect: YUK-22612");
////        final int EXPECTED_COUNT = 15;
////        assertThat(supportPage.getSupportSectionPageList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
//    }
//
//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
//    public void supportDetails_SupportPagesBatteryNodeAnalysis_LinkCorrect() {
//
//        throw new SkipException("Development Defect created: YUK-22612");
////        softly = new SoftAssertions();
////        softly.assertThat(supportPageTitles).contains("Battery Node Analysis");
////        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.BATTERY_NODE_ANALYSIS);
////        softly.assertAll();
//    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportPage_SupportPagesDataStreamingDeviceAttributes_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Data Streaming Device Attributes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DATA_STREAMING_DEVICE_ATTRIBUTES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesDBMigration_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Database Migration");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DATABASE_MIGRATION);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesDeviceDefinitions_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Device Definitions");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.DEVICE_DEFINITIONS);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesErrorCodes_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Error Codes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.ERROR_CODES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesEventLog_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Event Log");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.EVENT_LOG);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesFileExportHistory_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("File Export History");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.FILE_EXPORT_HISTORY);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesLocalizationHelper_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Localization Helper");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.LOCALIZATION_HELPER);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesLogExplorerLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Log Explorer");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.LOG_EXPLORER);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesManageIndexesLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Manage Indexes");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.MANAGE_INDEXES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesRouteUsageLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Route Usage");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.ROUTE_USAGE);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesSystemHealthLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("System Health");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.SYSTEM_HEALTH);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesSystemInfoLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("System Info");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.SYSTEM_INFO);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesThirdPartyLibrariesLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Third Party Libraries");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.THIRD_PARTY_LIBRARIES);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_SupportPagesThreadDumpLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPageTitles).contains("Thread Dump");
        softly.assertThat(supportPageLinks).contains(baseUrl + Urls.Support.THREAD_DUMP);
        softly.assertAll();
    }

    // ================================================================================
    // Manuals Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_ManualsPages_CountCorrect() {
        final int EXPECTED_COUNT = 1;
        assertThat(supportPage.getManualsSectionPageList().getSimpleListItems().size()).isEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_ManualsYukonManualsLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(manualsPageTitles).contains("Yukon Manuals");
        softly.assertThat(manualsPageLinks).contains(Urls.Manuals.YUKON_MANUALS);
        softly.assertAll();
    }

    // ================================================================================
    // Today's Logs
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsPages_CountCorrect() {
        final int EXPECTED_COUNT = 13;
        assertThat(supportPage.getTodaysLogsSectionPageList().getSimpleListItems().size()).isGreaterThanOrEqualTo(EXPECTED_COUNT);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsCalcLink_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Calc");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.CALC_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsCapcontrol_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Capcontrol");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.CAPCONTROL_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsDispatch_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Dispatch");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.DISPATCH_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsFdr_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Fdr");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.FDR_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsLoadManagement_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Loadmanagement");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.LOADMANGEMENT_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsMacs_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Macs");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.MACS_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsMessageBroker_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("MessageBroker");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.MESSAGE_BROKER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsNotificationServer_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("NotificationServer");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.NOTIFICATION_SERVER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsPorter_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Porter");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.PORTER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsScanner_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Scanner");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.SCANNER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsServiceManager_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("ServiceManager");
        softly.assertThat(todaysLogsPageLinks).contains(baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.SERVICE_MANAGER_LOG
                + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsWatchdog_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Watchdog");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.WATCHDOG_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsWebserver_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(todaysLogsPageTitles).contains("Webserver");
        softly.assertThat(todaysLogsPageLinks).contains(
                baseUrl + Urls.Support.LOGGING_VIEW + Urls.Support.WEBSERVER_LOG + supportPage.getCurrentLogTimeStamp() + ".log");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_TodaysLogsViewAllLogs_LinkCorrect() {
        softly = new SoftAssertions();

        softly.assertThat(supportPage.getViewAllLogsText()).isEqualTo("View All Logs");
        softly.assertThat(supportPage.getViewAllLogsLink())
                .isEqualTo(baseUrl + Urls.Support.LOGGING_MENU + Urls.Support.VIEW_ALL_LOGS);
        softly.assertAll();
    }

    // ================================================================================
    // Database Info
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void supportDetails_DBInfoDBValidation_LinkCorrect() {
        softly = new SoftAssertions();
        softly.assertThat(supportPage.getDatabaseValidationText()).isEqualTo("Database Validation");
        softly.assertThat(supportPage.getDatabaseValidationLink()).isEqualTo(baseUrl + Urls.Support.DATABASE_VALIDATION);
        softly.assertAll();
    }
}
