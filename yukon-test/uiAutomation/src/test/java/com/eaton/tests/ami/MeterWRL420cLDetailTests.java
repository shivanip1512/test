package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.WRL420cLMeterDetailsPage;

import org.assertj.core.api.SoftAssertions;

public class MeterWRL420cLDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private WRL420cLMeterDetailsPage meterDetailsPage;
    private final int DEVICEID = 1202;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.Ami.METER_DETAIL + DEVICEID);
        meterDetailsPage = new WRL420cLMeterDetailsPage(driverExt, DEVICEID);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
            refreshPage(meterDetailsPage);
    }

    // ================================================================================
    // Overall Page Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Panel_CountCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getPanels().getPanels().size()).isEqualTo(11);
        softly.assertThat(
                meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_INFO_PANEL_INDEX).getPanelName()).isEqualTo("Meter Information");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_READINGS_PANEL_INDEX).getPanelName()).isEqualTo("Meter Readings");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.WIFI_CONNECTION_PANEL_INDEX).getPanelName()).isEqualTo("Wi-Fi Connection");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.NETWORK_INFO_PANEL_INDEX).getPanelName()).isEqualTo("Network Information");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.NOTES_PANEL_INDEX).getPanelName()).isEqualTo("Notes");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.DEVICE_GROUP_PANEL_INDEX).getPanelName()).isEqualTo("Device Groups");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_TREND_PANEL_INDEX).getPanelName()).isEqualTo("Meter Trend");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_EVENTS_PANEL_INDEX).getPanelName()).isEqualTo("Meter Events");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.OUTAGES_PANEL_INDEX).getPanelName()).isEqualTo("Outages");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.TOU_PANEL_INDEX).getPanelName()).isEqualTo("Time of Use");
        softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.DEVICE_CONFIG_INDEX).getPanelName()).isEqualTo("Device Configuration");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Info Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfo_LabelsCorrect() {
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
    public void meterWRL420cLDetail_MeterInfo_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().size()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(0)).contains("AT Detail WRL-420cL");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(1)).contains("53000000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(2)).contains(MeterEnums.MeterType.WRL420CL.getMeterType());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(3)).contains("53000000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(4)).contains(MeterEnums.MeterType.WRL420CL.getManufacturer().getManufacturer());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(5)).contains(MeterEnums.MeterType.WRL420CL.getModel());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(6)).contains("Enabled");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Readings Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadings_LabelsCorrect() {
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
    public void meterWRL420cLDetail_MeterReadings_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().size()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(0)).contains("16,694.008 kWH 08/26/2020 13:08:06");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(1)).contains("16,694.008 kWH 08/26/2020 13:08:06");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(2)).contains("0.000");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(3)).contains("2.414 kW 08/26/2020 13:14:15");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(4)).contains("0.166 kW 08/26/2020 13:08:06");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(5)).contains("242.0 Volts 08/26/2020 13:12:46");
        softly.assertAll();
    }

    // ================================================================================
    // Wi-Fi Connection Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnection_LabelsCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().size()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(0)).contains("Communication Status");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(1)).contains("RSSI");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnection_ValuesCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().size()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(0)).contains("Connected  08/26/2020 13:13:44");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(1)).contains("-33 dBm 08/26/2020 13:13:15");
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
    // Meter Events Section
    // ================================================================================

    // ================================================================================
    // Outages Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Outages_LabelsCorrect() {
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
    public void meterWRL420cLDetail_Outages_ValuesCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	final int EXPECTED_COUNT = 3;
//    	final int RFN_BLINK_COUNT_ROW = 0;
//    	final int RFN_OUTAGE_COUNT_ROW = 1;
//    	final int BLINK_COUNT_ROW = 2;
//    	final String EXPECTED_RFN_BLINK_COUNT_VALUE = "5 Counts 08/26/2020 13:15:09";
//    	final String EXPECTED_RFN_OUTAGE_COUNT_VALUE = "2 Counts 08/26/2020 13:15:13";
//    	final String EXPECTED_BLINK_COUNT_VALUE = "7 Counts 08/26/2020 13:15:13";
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
    public void meterWRL420cLDetail_TimeOfUse_LabelsCorrect() {
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

    public void meterWRL420cLDetail_TimeOfUse_ValuesCorrect() {

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().size()).isEqualTo(8);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(0)).contains("5,727.212 kWH 08/26/2020 13:23:29");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(1)).contains("2.414 kW 08/26/2020 13:16:30");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(2)).contains("2.329 kW 08/26/2020 13:16:38");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(3)).contains("2.329 kW 08/26/2020 13:16:38");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(4)).contains("3,360.728 kWH 08/26/2020 13:23:45");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(5)).contains("1.321 kW 08/26/2020 13:16:44");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(6)).contains("3,079.871 kWH 08/26/2020 13:23:51");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(7)).contains("2.113 kW 08/26/2020 13:16:55");
        softly.assertAll();
    }

    // ================================================================================
    // Device Configuration Section
    // ================================================================================

    // ================================================================================
    // Actions Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Delete_Success() {
        final int DEVICEID = 1233;
        final String EXPECTED_MSG = "Meter AT Delete " + MeterEnums.MeterType.WRL420CL.getMeterType() + " deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + DEVICEID);

        WRL420cLMeterDetailsPage meterDetailsPage = new WRL420cLMeterDetailsPage(driverExt, DEVICEID);

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
