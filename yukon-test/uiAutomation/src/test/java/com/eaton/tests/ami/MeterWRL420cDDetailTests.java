package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.panels.DeviceConfigPanel;
import com.eaton.elements.panels.DeviceGroupsPanel;
import com.eaton.elements.panels.DisconnectPanel;
import com.eaton.elements.panels.MeterEventsPanel;
import com.eaton.elements.panels.MeterInfoPanel;
import com.eaton.elements.panels.MeterReadingsPanel;
import com.eaton.elements.panels.MeterTrendPanel;
import com.eaton.elements.panels.NetworkInfoPanel;
import com.eaton.elements.panels.NotesPanel;
import com.eaton.elements.panels.OutagesPanel;
import com.eaton.elements.panels.TimeOfUsePanel;
import com.eaton.elements.panels.WiFiConnectionPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterConstants;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MeterDetailsPage;
import org.assertj.core.api.SoftAssertions;

public class MeterWRL420cDDetailTests extends SeleniumTestSetup {

	
    private DriverExtensions driverExt;
    private MeterDetailsPage meterDetailsPage;
    private final int DEVICEID = 1234;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Ami.METER_DETAIL + DEVICEID);
        meterDetailsPage = new MeterDetailsPage(driverExt, DEVICEID);
    }
    
    //================================================================================
    // Meter Info Section
    //================================================================================

	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
	public void meterWRL420cDDetail_MeterInfo() {
		MeterInfoPanel meterInfoPanel = meterDetailsPage.getMeterInfoPanel();
	    assertThat(meterInfoPanel.getPanel()).isNotNull();
	}
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoDeviceName() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Device Name";
    	final String EXPECTED_VALUE = "AT Detail WRL-420cD";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoMeterNumber() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Meter Number";
    	final String EXPECTED_VALUE = "53100000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoType() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Type";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CD.getMeterType();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoSerialNumber() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Serial Number";
    	final String EXPECTED_VALUE = "53100000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoManufacturer() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Manufacturer";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CD.getManufacturer();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterInfoModel() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Model";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CD.getModel();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	//================================================================================
    // Meter Readings Section
    //================================================================================

    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadings() {
    	MeterReadingsPanel meterReadingsPanel = meterDetailsPage.getMeterReadingsPanel();
        assertThat(meterReadingsPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsUsageReading() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Usage Reading";
    	final String EXPECTED_VALUE = "16,717.612 kWH 08/26/2020 13:40:36";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsPreviousUsageReading() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Previous";
    	final String EXPECTED_VALUE = "16,717.612 kWH 08/26/2020 13:40:36";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsTotalConsumption() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Total Consumption";
    	final String EXPECTED_VALUE = "0.000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsPeakDemand() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Peak Demand";
    	final String EXPECTED_VALUE = "3.245 kW 08/26/2020 13:41:39";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsDemand() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Demand";
    	final String EXPECTED_VALUE = "0.166 kW 08/26/2020 13:40:36";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterReadingsVoltage() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Voltage";
    	final String EXPECTED_VALUE = "243.4 Volts 08/26/2020 13:41:11";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Wi-Fi Connection Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_WiFiConnection() {
    	WiFiConnectionPanel wifiConnectionPanel = meterDetailsPage.getWiFiConnectionPanel();
        assertThat(wifiConnectionPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_WiFiConnectionCommStatus() {
    	final String EXPECTED_LABEL = "Communication Status";
    	final String EXPECTED_VALUE = "Connected  08/26/2020 13:42:56";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getCommStatusLabel()).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getCommStatusValue()).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_WiFiConnectionRSSI() {
    	final String EXPECTED_LABEL = "RSSI";
    	final String EXPECTED_VALUE = "-43 dBm 08/26/2020 13:42:30";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getRSSILabel()).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getRSSIValue()).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Network Information Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_NetworkInfo() {
    	NetworkInfoPanel networkInfoPanel = meterDetailsPage.getNetworkInfoPanel();
        assertThat(networkInfoPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Notes Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Notes() {
    	NotesPanel notesPanel = meterDetailsPage.getNotesPanel();
        assertThat(notesPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Device Groups Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_DeviceGroups() {
    	DeviceGroupsPanel deviceGroupsPanel = meterDetailsPage.getDeviceGroupsPanel();
        assertThat(deviceGroupsPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Meter Trend Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterTrend() {
    	MeterTrendPanel meterTrendPanel = meterDetailsPage.getMeterTrendPanel();
        assertThat(meterTrendPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Disconnect Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Disconnect() {
    	DisconnectPanel disconnectPanel = meterDetailsPage.getDisconnectPanel();
    	
        assertThat(disconnectPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_DisconnectStatus() {   	
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Disconnect Status";
    	final String EXPECTED_VALUE = "08/26/2020 13:43:21";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getDisconnectPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getDisconnectPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Meter Events Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_MeterEvents() {
    	MeterEventsPanel meterEventsPanel = meterDetailsPage.getMeterEventsPanel();
        assertThat(meterEventsPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Outages Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_Outages() {
    	OutagesPanel outagesPanel = meterDetailsPage.getOutagesPanel();
        assertThat(outagesPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_OutagesRFNBlinkCount() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "RFN Blink Count";
    	final String EXPECTED_VALUE = "8 Counts 08/26/2020 13:44:06";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_OutagesBlinkCount() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Blink Count";
    	final String EXPECTED_VALUE = "11 Counts 08/26/2020 13:57:53";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_OutagesRFNOutageCount() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "RFN Outage Count";
    	final String EXPECTED_VALUE = "3 Counts 08/26/2020 13:44:06";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Time of Use Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUse() {
    	TimeOfUsePanel timeOfUsePanel = meterDetailsPage.getTimeOfUsePanel();
        assertThat(timeOfUsePanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateA() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Usage Rate A";
    	final String EXPECTED_VALUE = "4,682.123 kWH 08/26/2020 13:54:28";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUsePeakDemandRateA() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Peak Demand Rate A";
    	final String EXPECTED_VALUE = "3.245 kW 08/26/2020 13:46:21";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateB() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Usage Rate B";
    	final String EXPECTED_VALUE = "3,682.360 kWH 08/26/2020 13:54:33";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUsePeakDemandRateB() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Peak Demand Rate B";
    	final String EXPECTED_VALUE = "2.537 kW 08/26/2020 13:46:21";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateC() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Usage Rate C";
    	final String EXPECTED_VALUE = "4,527.000 kWH 08/26/2020 13:54:38";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUsePeakDemandRateC() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Peak Demand Rate C";
    	final String EXPECTED_VALUE = "3.127 kW 08/26/2020 13:46:22";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateD() {
    	final int ROW = 6;
    	final String EXPECTED_LABEL = "Usage Rate D";
    	final String EXPECTED_VALUE = "3,826.130 kWH 08/26/2020 13:54:59";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_TimeOfUsePeakDemandRateD() {
    	final int ROW = 7;
    	final String EXPECTED_LABEL = "Peak Demand Rate D";
    	final String EXPECTED_VALUE = "2.845 kW 08/26/2020 13:46:23";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Device Configuration Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_DeviceConfig() {
    	DeviceConfigPanel deviceConfigPanel = meterDetailsPage.getDeviceConfigPanel();
        assertThat(deviceConfigPanel.getPanel()).isNotNull();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
    }
}
