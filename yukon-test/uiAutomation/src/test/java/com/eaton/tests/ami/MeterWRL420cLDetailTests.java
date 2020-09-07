package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
    private boolean requiresRefresh = false;
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
    	if(requiresRefresh) {
    		refreshPage(meterDetailsPage);
    		requiresRefresh = false;
    	}
    }
    
    //================================================================================
    // Overall Page Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
	public void meterWRL420cLDetail_Panel_Count() {
		final int EXPECTED_COUNT = 11;
		final String EXPECTED_METER_INFO_PANEL_TEXT = "Meter Information";
		final String EXPECTED_METER_READINGS_PANEL_TEXT = "Meter Readings";
		final String EXPECTED_WIFI_CONNECTION_PANEL_TEXT = "Wi-Fi Connection";
		final String EXPECTED_NETWORK_INFO_PANEL_TEXT = "Network Information";
		final String EXPECTED_NOTES_PANEL_TEXT = "Notes";
		final String EXPECTED_DEVICE_GROUP_PANEL_TEXT = "Device Groups";
		final String EXPECTED_METER_TREND_PANEL_TEXT = "Meter Trend";
		final String EXPECTED_METER_EVENTS_PANEL_TEXT = "Meter Events";
		final String EXPECTED_OUTAGES_PANEL_TEXT = "Outages";
		final String EXPECTED_TOU_PANEL_TEXT = "Time of Use";
		final String EXPECTED_DEVICE_CONFIG_PANEL_TEXT = "Device Configuration";
		
		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(meterDetailsPage.getPanels().getPanels().size()).isEqualTo(EXPECTED_COUNT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_INFO_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_METER_INFO_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_READINGS_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_METER_READINGS_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.WIFI_CONNECTION_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_WIFI_CONNECTION_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.NETWORK_INFO_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_NETWORK_INFO_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.NOTES_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_NOTES_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.DEVICE_GROUP_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_DEVICE_GROUP_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_TREND_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_METER_TREND_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.METER_EVENTS_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_METER_EVENTS_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.OUTAGES_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_OUTAGES_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.TOU_PANEL_INDEX).getPanelName()).isEqualTo(EXPECTED_TOU_PANEL_TEXT);
		softly.assertThat(meterDetailsPage.getPanels().getPanels().get(WRL420cLMeterDetailsPage.DEVICE_CONFIG_INDEX).getPanelName()).isEqualTo(EXPECTED_DEVICE_CONFIG_PANEL_TEXT);
		softly.assertAll();
	}
    
    //================================================================================
    // Meter Info Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfo_Labels() {
    	final int EXPECTED_COUNT = 7;
    	final int DEVICE_NAME_ROW = 0;
    	final int METER_NUMBER_ROW = 1;
    	final int TYPE_ROW = 2;
    	final int SERIAL_NUMBER_ROW = 3;
    	final int MANUFACTURER_ROW = 4;
    	final int MODEL_ROW = 5;
    	final int STATUS_ROW = 6;
    	final String EXPECTED_DEVICE_NAME_LABEL = "Device Name";
    	final String EXPECTED_METER_NUMBER_LABEL = "Meter Number";
    	final String EXPECTED_TYPE_LABEL = "Type";
    	final String EXPECTED_SERIAL_NUMBER_LABEL = "Serial Number";
    	final String EXPECTED_MANUFACTURER_LABEL = "Manufacturer";
    	final String EXPECTED_MODEL_LABEL = "Model";
    	final String EXPECTED_STATUS_LABEL = "Status";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(DEVICE_NAME_ROW)).contains(EXPECTED_DEVICE_NAME_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(METER_NUMBER_ROW)).contains(EXPECTED_METER_NUMBER_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(TYPE_ROW)).contains(EXPECTED_TYPE_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(SERIAL_NUMBER_ROW)).contains(EXPECTED_SERIAL_NUMBER_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(MANUFACTURER_ROW)).contains(EXPECTED_MANUFACTURER_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(MODEL_ROW)).contains(EXPECTED_MODEL_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(STATUS_ROW)).contains(EXPECTED_STATUS_LABEL);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfo_Values() {
    	final int EXPECTED_COUNT = 7;
    	final int DEVICE_NAME_ROW = 0;
    	final int METER_NUMBER_ROW = 1;
    	final int TYPE_ROW = 2;
    	final int SERIAL_NUMBER_ROW = 3;
    	final int MANUFACTURER_ROW = 4;
    	final int MODEL_ROW = 5;
    	final int STATUS_ROW = 6;
    	final String EXPECTED_DEVICE_NAME_VALUE = "AT Detail WRL-420cL";
    	final String EXPECTED_METER_NUMBER_VALUE = "53000000";
    	final String EXPECTED_TYPE_VALUE = MeterEnums.MeterType.WRL420CL.getMeterType();
    	final String EXPECTED_SERIAL_NUMBER_VALUE = "53000000";
    	final String EXPECTED_MANUFACTURER_VALUE = MeterEnums.MeterType.WRL420CL.getManufacturer().getManufacturer();;
    	final String EXPECTED_MODEL_VALUE = MeterEnums.MeterType.WRL420CL.getModel();
    	final String EXPECTED_STATUS_VALUE = "Enabled";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(DEVICE_NAME_ROW)).contains(EXPECTED_DEVICE_NAME_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(METER_NUMBER_ROW)).contains(EXPECTED_METER_NUMBER_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(TYPE_ROW)).contains(EXPECTED_TYPE_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(SERIAL_NUMBER_ROW)).contains(EXPECTED_SERIAL_NUMBER_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(MANUFACTURER_ROW)).contains(EXPECTED_MANUFACTURER_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(MODEL_ROW)).contains(EXPECTED_MODEL_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(STATUS_ROW)).contains(EXPECTED_STATUS_VALUE);
    	softly.assertAll();
    }
	
	//================================================================================
    // Meter Readings Section
    //================================================================================
	
	@Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadings_Labels() {
    	final int EXPECTED_COUNT = 6;
    	final int USAGE_READING_ROW = 0;
    	final int PREVIOUS_ROW = 1;
    	final int TOTAL_CONSUMPTION_ROW = 2;
    	final int PEAK_DEMAND_ROW = 3;
    	final int DEMAND_ROW = 4;
    	final int VOLTAGE_ROW = 5;
    	final String EXPECTED_USAGE_READING_LABEL = "Usage Reading";
    	final String EXPECTED_PREVIOUS_LABEL = "Previous";
    	final String EXPECTED_TOTAL_CONSUMPTION_LABEL = "Total Consumption";
    	final String EXPECTED_PEAK_DEMAND_LABEL = "Peak Demand";
    	final String EXPECTED_DEMAND_LABEL = "Demand";
    	final String EXPECTED_VOLTAGE_LABEL = "Voltage";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(USAGE_READING_ROW)).contains(EXPECTED_USAGE_READING_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(PREVIOUS_ROW)).contains(EXPECTED_PREVIOUS_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(TOTAL_CONSUMPTION_ROW)).contains(EXPECTED_TOTAL_CONSUMPTION_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(PEAK_DEMAND_ROW)).contains(EXPECTED_PEAK_DEMAND_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(DEMAND_ROW)).contains(EXPECTED_DEMAND_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(VOLTAGE_ROW)).contains(EXPECTED_VOLTAGE_LABEL);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadings_Values() {
    	final int EXPECTED_COUNT = 6;
    	final int USAGE_READING_ROW = 0;
    	final int PREVIOUS_ROW = 1;
    	final int TOTAL_CONSUMPTION_ROW = 2;
    	final int PEAK_DEMAND_ROW = 3;
    	final int DEMAND_ROW = 4;
    	final int VOLTAGE_ROW = 5;
    	final String EXPECTED_USAGE_READING_VALUE = "16,694.008 kWH 08/26/2020 13:08:06";
    	final String EXPECTED_PREVIOUS_VALUE = "16,694.008 kWH 08/26/2020 13:08:06";
    	final String EXPECTED_TOTAL_CONSUMPTION_VALUE = "0.000";
    	final String EXPECTED_PEAK_DEMAND_VALUE = "2.414 kW 08/26/2020 13:14:15";
    	final String EXPECTED_DEMAND_VALUE = "0.166 kW 08/26/2020 13:08:06";
    	final String EXPECTED_VOLTAGE_VALUE = "242.0 Volts 08/26/2020 13:12:46";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(USAGE_READING_ROW)).contains(EXPECTED_USAGE_READING_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(PREVIOUS_ROW)).contains(EXPECTED_PREVIOUS_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(TOTAL_CONSUMPTION_ROW)).contains(EXPECTED_TOTAL_CONSUMPTION_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(PEAK_DEMAND_ROW)).contains(EXPECTED_PEAK_DEMAND_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(DEMAND_ROW)).contains(EXPECTED_DEMAND_VALUE);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(VOLTAGE_ROW)).contains(EXPECTED_VOLTAGE_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Wi-Fi Connection Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnection_Labels() {
    	final int EXPECTED_COUNT = 2;
    	final int COMM_STATUS_ROW = 0;
    	final int RSSI_ROW = 1;
    	final String EXPECTED_COMM_STATUS_LABEL = "Communication Status";
    	final String EXPECTED_RSSI_LABEL = "RSSI";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(COMM_STATUS_ROW)).contains(EXPECTED_COMM_STATUS_LABEL);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getLabelEntries().get(RSSI_ROW)).contains(EXPECTED_RSSI_LABEL);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnection_Values() {
    	final int EXPECTED_COUNT = 2;
    	final int COMM_STATUS_ROW = 0;
    	final int RSSI_ROW = 1;
    	final String EXPECTED_COMM_STATUS_VALUE = "Connected  08/26/2020 13:13:44";
    	final String EXPECTED_RSSI_VALUE = "-33 dBm 08/26/2020 13:13:15";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(COMM_STATUS_ROW)).contains(EXPECTED_COMM_STATUS_VALUE);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getValueEntries().get(RSSI_ROW)).contains(EXPECTED_RSSI_VALUE);
    	softly.assertAll();
    }
    
    
    //================================================================================
    // Network Information Section
    //================================================================================
    
    
    //================================================================================
    // Notes Section
    //================================================================================
    
    
    //================================================================================
    // Device Groups Section
    //================================================================================
    
    
    //================================================================================
    // Meter Trend Section
    //================================================================================
 
    
    //================================================================================
    // Meter Events Section
    //================================================================================
    
    
    //================================================================================
    // Outages Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Outages_Labels() {   	
    	final int EXPECTED_COUNT = 3;
    	final int RFN_BLINK_COUNT_ROW = 0;
    	final int RFN_OUTAGE_COUNT_ROW = 1;
    	final int BLINK_COUNT_ROW = 2;
    	final String EXPECTED_RFN_BLINK_COUNT_LABEL = "RFN Blink Count";
    	final String EXPECTED_RFN_OUTAGE_COUNT_LABEL = "RFN Outage Count";
    	final String EXPECTED_BLINK_COUNT_LABEL = "Blink Count";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(RFN_BLINK_COUNT_ROW)).contains(EXPECTED_RFN_BLINK_COUNT_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(RFN_OUTAGE_COUNT_ROW)).contains(EXPECTED_RFN_OUTAGE_COUNT_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(BLINK_COUNT_ROW)).contains(EXPECTED_BLINK_COUNT_LABEL);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Outages_Values() {   	
    	final int EXPECTED_COUNT = 3;
    	final int RFN_BLINK_COUNT_ROW = 0;
    	final int RFN_OUTAGE_COUNT_ROW = 1;
    	final int BLINK_COUNT_ROW = 2;
    	final String EXPECTED_RFN_BLINK_COUNT_VALUE = "5 Counts 08/26/2020 13:15:09";
    	final String EXPECTED_RFN_OUTAGE_COUNT_VALUE = "2 Counts 08/26/2020 13:15:13";
    	final String EXPECTED_BLINK_COUNT_VALUE = "7 Counts 08/26/2020 13:15:13";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(RFN_BLINK_COUNT_ROW)).contains(EXPECTED_RFN_BLINK_COUNT_VALUE);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(RFN_OUTAGE_COUNT_ROW)).contains(EXPECTED_RFN_OUTAGE_COUNT_VALUE);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(BLINK_COUNT_ROW)).contains(EXPECTED_BLINK_COUNT_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Time of Use Section
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUse_Labels() {   	
    	final int EXPECTED_COUNT = 8;
    	final int USAGE_RATE_A_ROW = 0;
    	final int PEAK_DEMAND_RATE_A_ROW = 1;
    	final int USAGE_RATE_B_ROW = 2;
    	final int PEAK_DEMAND_RATE_B_ROW = 3;
    	final int USAGE_RATE_C_ROW = 4;
    	final int PEAK_DEMAND_RATE_C_ROW = 5;
    	final int USAGE_RATE_D_ROW = 6;
    	final int PEAK_DEMAND_RATE_D_ROW = 7;
    	final String EXPECTED_USAGE_RATE_A_LABEL = "Usage Rate A";
    	final String EXPECTED_PEAK_DEMAND_RATE_A_LABEL = "Peak Demand Rate A";
    	final String EXPECTED_USAGE_RATE_B_LABEL = "Usage Rate B";
    	final String EXPECTED_PEAK_DEMAND_RATE_B_LABEL = "Peak Demand Rate B";
    	final String EXPECTED_USAGE_RATE_C_LABEL = "Usage Rate C";
    	final String EXPECTED_PEAK_DEMAND_RATE_C_LABEL = "Peak Demand Rate C";
    	final String EXPECTED_USAGE_RATE_D_LABEL = "Usage Rate D";
    	final String EXPECTED_PEAK_DEMAND_RATE_D_LABEL = "Peak Demand Rate D";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(USAGE_RATE_A_ROW)).contains(EXPECTED_USAGE_RATE_A_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(PEAK_DEMAND_RATE_A_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_A_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(USAGE_RATE_B_ROW)).contains(EXPECTED_USAGE_RATE_B_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(PEAK_DEMAND_RATE_B_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_B_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(USAGE_RATE_C_ROW)).contains(EXPECTED_USAGE_RATE_C_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(PEAK_DEMAND_RATE_C_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_C_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(USAGE_RATE_D_ROW)).contains(EXPECTED_USAGE_RATE_D_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(PEAK_DEMAND_RATE_D_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_D_LABEL);
    	softly.assertAll();
    }
    
    public void meterWRL420cLDetail_TimeOfUse_Values() {   	
    	final int EXPECTED_COUNT = 8;
    	final int USAGE_RATE_A_ROW = 0;
    	final int PEAK_DEMAND_RATE_A_ROW = 1;
    	final int USAGE_RATE_B_ROW = 2;
    	final int PEAK_DEMAND_RATE_B_ROW = 3;
    	final int USAGE_RATE_C_ROW = 4;
    	final int PEAK_DEMAND_RATE_C_ROW = 5;
    	final int USAGE_RATE_D_ROW = 6;
    	final int PEAK_DEMAND_RATE_D_ROW = 7;
    	final String EXPECTED_USAGE_RATE_A_VALUE = "5,727.212 kWH 08/26/2020 13:23:29";
    	final String EXPECTED_PEAK_DEMAND_RATE_A_VALUE = "2.414 kW 08/26/2020 13:16:30";
    	final String EXPECTED_USAGE_RATE_B_VALUE = "2.329 kW 08/26/2020 13:16:38";
    	final String EXPECTED_PEAK_DEMAND_RATE_B_VALUE = "2.329 kW 08/26/2020 13:16:38";
    	final String EXPECTED_USAGE_RATE_C_VALUE = "3,360.728 kWH 08/26/2020 13:23:45";
    	final String EXPECTED_PEAK_DEMAND_RATE_C_VALUE = "1.321 kW 08/26/2020 13:16:44";
    	final String EXPECTED_USAGE_RATE_D_VALUE = "3,079.871 kWH 08/26/2020 13:23:51";
    	final String EXPECTED_PEAK_DEMAND_RATE_D_VALUE = "2.113 kW 08/26/2020 13:16:55";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().size()).isEqualTo(EXPECTED_COUNT);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(USAGE_RATE_A_ROW)).contains(EXPECTED_USAGE_RATE_A_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(PEAK_DEMAND_RATE_A_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_A_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(USAGE_RATE_B_ROW)).contains(EXPECTED_USAGE_RATE_B_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(PEAK_DEMAND_RATE_B_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_B_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(USAGE_RATE_C_ROW)).contains(EXPECTED_USAGE_RATE_C_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(PEAK_DEMAND_RATE_C_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_C_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(USAGE_RATE_D_ROW)).contains(EXPECTED_USAGE_RATE_D_VALUE);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(PEAK_DEMAND_RATE_D_ROW)).contains(EXPECTED_PEAK_DEMAND_RATE_D_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Device Configuration Section
    //================================================================================
       
    //================================================================================
    // Actions Section
    //================================================================================
    
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Delete_Success() {
    	requiresRefresh = true;
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
