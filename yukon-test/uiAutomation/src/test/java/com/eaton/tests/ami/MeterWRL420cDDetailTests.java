package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.WRL420cDMeterDetailsPage;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;

public class MeterWRL420cDDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private WRL420cDMeterDetailsPage meterDetailsPage;
    private final int DEVICEID = 1234;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.Ami.METER_DETAIL + DEVICEID);
        meterDetailsPage = new WRL420cDMeterDetailsPage(driverExt, DEVICEID);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(meterDetailsPage);
    }

    // ================================================================================
    // Overall Page Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Panel_CountCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getPanels().getPanels().size()).isEqualTo(12);
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.METER_INFO_PANEL_INDEX).getPanelName()).isEqualTo("Meter Information");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.METER_READINGS_PANEL_INDEX).getPanelName()).isEqualTo("Meter Readings");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.WIFI_CONNECTION_PANEL_INDEX).getPanelName()).isEqualTo("Wi-Fi Connection");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.NETWORK_INFO_PANEL_INDEX).getPanelName()).isEqualTo("Network Information");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.NOTES_PANEL_INDEX).getPanelName()).isEqualTo("Notes");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.DEVICE_GROUP_PANEL_INDEX).getPanelName()).isEqualTo("Device Groups");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.METER_TREND_PANEL_INDEX).getPanelName()).isEqualTo("Meter Trend");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.DISCONNECT_PANEL_INDEX).getPanelName()).isEqualTo("Disconnect");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.METER_EVENTS_PANEL_INDEX).getPanelName()).isEqualTo("Meter Events");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.OUTAGES_PANEL_INDEX).getPanelName()).isEqualTo("Outages");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.TOU_PANEL_INDEX).getPanelName()).isEqualTo("Time of Use");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cDMeterDetailsPage.DEVICE_CONFIG_INDEX).getPanelName()).isEqualTo("Device Configuration");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Info Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfo_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().size()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(0)).contains("Device Name");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(1)).contains("Meter Number");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(2)).contains("Type");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(3)).contains("Serial Number");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(4)).contains("Manufacturer");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(5)).contains("Model");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(6)).contains("Status");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfo_ValuesCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().size()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(0)).contains("AT Detail WRL-420cD");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(1)).contains("53100000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(2)).contains(MeterEnums.MeterType.WRL420CD.getMeterType());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(3)).contains("53100000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(4)).contains(MeterEnums.MeterType.WRL420CD.getManufacturer().getManufacturer());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(5)).contains(MeterEnums.MeterType.WRL420CD.getModel());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(6)).contains("Enabled");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Readings Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadings_LabelsCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().size()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(0)).contains("Usage Reading");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(1)).contains("Previous");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(2)).contains("Total Consumption");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(3)).contains("Peak Demand");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(4)).contains("Demand");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(5)).contains("Voltage");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadings_ValuesCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().size()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(0)).contains("16,717.612 kWH 08/26/2020 13:40:36");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(1)).contains("16,717.612 kWH 08/26/2020 13:40:36");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(2)).contains("0.000");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(3)).contains("3.245 kW 08/26/2020 13:41:39");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(4)).contains("0.166 kW 08/26/2020 13:40:36");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(5)).contains("243.4 Volts 08/26/2020 13:41:11");
        softly.assertAll();
    }

    // ================================================================================
    // Wi-Fi Connection Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_WiFiConnection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().size()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(0)).contains("Communication Status");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(1)).contains("RSSI");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_WiFiConnection_ValuesCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().size()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(0)).contains("Connected  08/26/2020 13:42:56");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(1)).contains("-43 dBm 08/26/2020 13:42:30");
        softly.assertAll();
    }

    // ================================================================================
    // Network Information Section
    // ================================================================================

    // ================================================================================
    // Notes Section
    // ================================================================================

    // ================================================================================
    // Device Groups Section
    // ================================================================================

    // ================================================================================
    // Meter Trend Section
    // ================================================================================

    // ================================================================================
    // Disconnect Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Disconnect_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getLabelEntries().size()).isEqualTo(1);
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getLabelEntries().get(0)).contains("Disconnect Status");
        softly.assertAll();
    }

    public void meterWRL420cDDetail_Disconnect_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getValueEntries().size()).isEqualTo(1);
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getValueEntries().get(0)).contains("08/26/2020 13:43:21");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Events Section
    // ================================================================================

    // ================================================================================
    // Outages Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Outages_LabelsCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	final int EXPECTED_COUNT = 3;
//    	final int RFN_BLINK_COUNT_ROW = 0;
//    	final int RFN_OUTAGE_COUNT_ROW = 1;
//    	final int BLINK_COUNT_ROW = 2;
//    	final String EXPECTED_RFN_BLINK_COUNT_LABEL = "RFN Blink Count";
//    	final String EXPECTED_RFN_OUTAGE_COUNT_LABEL = "RFN Outage Count";
//    	final String EXPECTED_BLINK_COUNT_LABEL = "Blink Count";
//    	
//    	SoftAssertions softly = new SoftAssertions();
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(RFN_BLINK_COUNT_ROW)).contains(EXPECTED_RFN_BLINK_COUNT_LABEL);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(RFN_OUTAGE_COUNT_ROW)).contains(EXPECTED_RFN_OUTAGE_COUNT_LABEL);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(BLINK_COUNT_ROW)).contains(EXPECTED_BLINK_COUNT_LABEL);
//    	softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Outages_ValuesCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	final int EXPECTED_COUNT = 3;
//    	final int RFN_BLINK_COUNT_ROW = 0;
//    	final int RFN_OUTAGE_COUNT_ROW = 1;
//    	final int BLINK_COUNT_ROW = 2;
//    	final String EXPECTED_RFN_BLINK_COUNT_VALUE = "8 Counts 08/26/2020 13:44:06";
//    	final String EXPECTED_RFN_OUTAGE_COUNT_VALUE = "3 Counts 08/26/2020 13:44:06";
//    	final String EXPECTED_BLINK_COUNT_VALUE = "11 Counts 08/26/2020 13:57:53";
//    	
//    	SoftAssertions softly = new SoftAssertions();
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(RFN_BLINK_COUNT_ROW)).contains(EXPECTED_RFN_BLINK_COUNT_VALUE);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(RFN_OUTAGE_COUNT_ROW)).contains(EXPECTED_RFN_OUTAGE_COUNT_VALUE);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(BLINK_COUNT_ROW)).contains(EXPECTED_BLINK_COUNT_VALUE);
//    	softly.assertAll();
    }

    // ================================================================================
    // Time of Use Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUse_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().size()).isEqualTo(8);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(0)).contains("Usage Rate A");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(1)).contains("Peak Demand Rate A");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(2)).contains("Usage Rate B");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(3)).contains("Peak Demand Rate B");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(4)).contains("Usage Rate C");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(5)).contains("Peak Demand Rate C");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(6)).contains("Usage Rate D");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(7)).contains("Peak Demand Rate D");
        softly.assertAll();
    }

    public void meterWRL420cDDetail_TimeOfUse_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().size()).isEqualTo(8);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(0)).contains("4,682.123 kWH 08/26/2020 13:54:28");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(1)).contains("3.245 kW 08/26/2020 13:46:21");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(2)).contains("3,682.360 kWH 08/26/2020 13:54:33");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(3)).contains("2.537 kW 08/26/2020 13:46:21");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(4)).contains("4,527.000 kWH 08/26/2020 13:54:38");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(5)).contains("3.127 kW 08/26/2020 13:46:22");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(6)).contains("3,826.130 kWH 08/26/2020 13:54:59");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(7)).contains("2.845 kW 08/26/2020 13:46:23");
        softly.assertAll();
    }

    // ================================================================================
    // Device Configuration Section
    // ================================================================================

    // ================================================================================
    // Actions Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Delete_Success() {
        final int DEVICEID = 1263;
        final String EXPECTED_MSG = "Meter AT Delete " + MeterEnums.MeterType.WRL420CD.getMeterType() + " deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + DEVICEID);

        WRL420cDMeterDetailsPage meterDetailsPage = new WRL420cDMeterDetailsPage(driverExt, DEVICEID);

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
