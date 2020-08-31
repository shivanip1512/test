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

public class MeterWRL420cLDetailTests extends SeleniumTestSetup {

	
    private DriverExtensions driverExt;
    private MeterDetailsPage meterDetailsPage;
    private final int DEVICEID = 1202;

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
	public void meterWRL420cLDetail_MeterInfo() {
		MeterInfoPanel meterInfoPanel = meterDetailsPage.getMeterInfoPanel();
	    assertThat(meterInfoPanel.getPanel()).isNotNull();
	}
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoDeviceName() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Device Name";
    	final String EXPECTED_VALUE = "AT Detail WRL-420cL";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoMeterNumber() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Meter Number";
    	final String EXPECTED_VALUE = "53000000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoType() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Type";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CL.getMeterType();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoSerialNumber() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Serial Number";
    	final String EXPECTED_VALUE = "53000000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoManufacturer() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Manufacturer";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CL.getManufacturer();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	@Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterInfoModel() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Model";
    	final String EXPECTED_VALUE = MeterConstants.WRL420CL.getModel();

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterInfoPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
	
	//================================================================================
    // Meter Readings Section
    //================================================================================

    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadings() {
    	MeterReadingsPanel meterReadingsPanel = meterDetailsPage.getMeterReadingsPanel();
        assertThat(meterReadingsPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsUsageReading() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Usage Reading";
    	final String EXPECTED_VALUE = "16,694.008 kWH 08/26/2020 13:08:06";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsPreviousUsageReading() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Previous";
    	final String EXPECTED_VALUE = "16,694.008 kWH 08/26/2020 13:08:06";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsTotalConsumption() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Total Consumption";
    	final String EXPECTED_VALUE = "0.000";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsPeakDemand() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Peak Demand";
    	final String EXPECTED_VALUE = "2.414 kW 08/26/2020 13:14:15";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsDemand() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Demand";
    	final String EXPECTED_VALUE = "0.166 kW 08/26/2020 13:08:06";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterReadingsVoltage() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Voltage";
    	final String EXPECTED_VALUE = "242.0 Volts 08/26/2020 13:12:46";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Wi-Fi Connection Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnection() {
    	WiFiConnectionPanel wifiConnectionPanel = meterDetailsPage.getWiFiConnectionPanel();
        assertThat(wifiConnectionPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnectionCommStatus() {
    	final String EXPECTED_LABEL = "Communication Status";
    	final String EXPECTED_VALUE = "Connected  08/26/2020 13:13:44";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getCommStatusLabel()).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getCommStatusValue()).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_WiFiConnectionRSSI() {
    	final String EXPECTED_LABEL = "RSSI";
    	final String EXPECTED_VALUE = "-33 dBm 08/26/2020 13:13:15";
    	
    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getRSSILabel()).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getRSSIValue()).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Network Information Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_NetworkInfo() {
    	NetworkInfoPanel networkInfoPanel = meterDetailsPage.getNetworkInfoPanel();
        assertThat(networkInfoPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Notes Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Notes() {
    	NotesPanel notesPanel = meterDetailsPage.getNotesPanel();
        assertThat(notesPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Device Groups Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_DeviceGroups() {
    	DeviceGroupsPanel deviceGroupsPanel = meterDetailsPage.getDeviceGroupsPanel();
        assertThat(deviceGroupsPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Meter Trend Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterTrend() {
    	MeterTrendPanel meterTrendPanel = meterDetailsPage.getMeterTrendPanel();
        assertThat(meterTrendPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Disconnect Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Disconnect() {
    	DisconnectPanel disconnectPanel = meterDetailsPage.getDisconnectPanel();
    	//There should not be a disconnect panel, because this is not a disconnect meter type.
        assertThat(disconnectPanel.getPanel()).isNull();
    }
    
    //================================================================================
    // Meter Events Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_MeterEvents() {
    	MeterEventsPanel meterEventsPanel = meterDetailsPage.getMeterEventsPanel();
        assertThat(meterEventsPanel.getPanel()).isNotNull();
    }
    
    //================================================================================
    // Outages Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_Outages() {
    	OutagesPanel outagesPanel = meterDetailsPage.getOutagesPanel();
        assertThat(outagesPanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_OutagesRFNBlinkCount() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "RFN Blink Count";
    	final String EXPECTED_VALUE = "5 Counts 08/26/2020 13:15:09";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_OutagesBlinkCount() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Blink Count";
    	final String EXPECTED_VALUE = "7 Counts 08/26/2020 13:15:13";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_OutagesRFNOutageCount() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "RFN Outage Count";
    	final String EXPECTED_VALUE = "2 Counts 08/26/2020 13:15:13";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getOutagesPanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Time of Use Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUse() {
    	TimeOfUsePanel timeOfUsePanel = meterDetailsPage.getTimeOfUsePanel();
        assertThat(timeOfUsePanel.getPanel()).isNotNull();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUseUsageRateA() {
    	final int ROW = 0;
    	final String EXPECTED_LABEL = "Usage Rate A";
    	final String EXPECTED_VALUE = "5,727.212 kWH 08/26/2020 13:23:29";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUsePeakDemandRateA() {
    	final int ROW = 1;
    	final String EXPECTED_LABEL = "Peak Demand Rate A";
    	final String EXPECTED_VALUE = "2.414 kW 08/26/2020 13:16:30";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUseUsageRateB() {
    	final int ROW = 2;
    	final String EXPECTED_LABEL = "Usage Rate B";
    	final String EXPECTED_VALUE = "4,526.197 kWH 08/26/2020 13:23:37";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUsePeakDemandRateB() {
    	final int ROW = 3;
    	final String EXPECTED_LABEL = "Peak Demand Rate B";
    	final String EXPECTED_VALUE = "2.329 kW 08/26/2020 13:16:38";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUseUsageRateC() {
    	final int ROW = 4;
    	final String EXPECTED_LABEL = "Usage Rate C";
    	final String EXPECTED_VALUE = "3,360.728 kWH 08/26/2020 13:23:45";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUsePeakDemandRateC() {
    	final int ROW = 5;
    	final String EXPECTED_LABEL = "Peak Demand Rate C";
    	final String EXPECTED_VALUE = "1.321 kW 08/26/2020 13:16:44";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUseUsageRateD() {
    	final int ROW = 6;
    	final String EXPECTED_LABEL = "Usage Rate D";
    	final String EXPECTED_VALUE = "3,079.871 kWH 08/26/2020 13:23:51";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_TimeOfUsePeakDemandRateD() {
    	final int ROW = 7;
    	final String EXPECTED_LABEL = "Peak Demand Rate D";
    	final String EXPECTED_VALUE = "2.113 kW 08/26/2020 13:16:55";

    	SoftAssertions softly = new SoftAssertions();
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getLabelEntries().get(ROW)).contains(EXPECTED_LABEL);
    	softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getValueEntries().get(ROW)).contains(EXPECTED_VALUE);
    	softly.assertAll();
    }
    
    //================================================================================
    // Device Configuration Section
    //================================================================================
    
    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLDetail_DeviceConfig() {
    	DeviceConfigPanel deviceConfigPanel = meterDetailsPage.getDeviceConfigPanel();
        assertThat(deviceConfigPanel.getPanel()).isNotNull();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
    }
}
