package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.WRL420cLMeterDetailsPage;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;

public class MeterWRL420cLDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private WRL420cLMeterDetailsPage meterDetailsPage;
    private String deviceId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        deviceId = TestDbDataType.MeterData.WRL_420CL_DETAIL_ID.getId().toString();
        navigate(Urls.Ami.METER_DETAIL + deviceId);
        meterDetailsPage = new WRL420cLMeterDetailsPage(driverExt, Integer.parseInt(deviceId));
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        if(getRefreshPage()) {
            refreshPage(meterDetailsPage);
        }
            
        setRefreshPage(false);
    }

    // ================================================================================
    // Overall Page Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_PanelNames_Correct() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getPanelList().getPanelCount()).isEqualTo(11);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getPanelName()).isEqualTo("Meter Information");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getPanelName()).isEqualTo("Meter Readings");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getPanelName()).isEqualTo("Wi-Fi Connection");
        softly.assertThat(meterDetailsPage.getNetworkInfoPanel().getPanelName()).isEqualTo("Network Information");
        softly.assertThat(meterDetailsPage.getNotesPanel().getPanelName()).isEqualTo("Notes");
        softly.assertThat(meterDetailsPage.getDeviceGroupsPanel().getPanelName()).isEqualTo("Device Groups");
        softly.assertThat(meterDetailsPage.getMeterTrendPanel().getPanelName()).isEqualTo("Meter Trend");
        softly.assertThat(meterDetailsPage.getMeterEventsPanel().getPanelName()).isEqualTo("Meter Events");
        softly.assertThat(meterDetailsPage.getOutagesPanel().getPanelName()).isEqualTo("Outages");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getPanelName()).isEqualTo("Time of Use");
        softly.assertThat(meterDetailsPage.getDeviceConfigPanel().getPanelName()).isEqualTo("Device Configuration");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Info Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_MeterInfo_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelCount()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(0)).isEqualTo("Device Name:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(1)).isEqualTo("Meter Number:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(2)).isEqualTo("Type:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(3)).isEqualTo("Serial Number:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(4)).isEqualTo("Manufacturer:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(5)).isEqualTo("Model:");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getLabelByRow(6)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_MeterInfo_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueCount()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(0)).isEqualTo("AT Detail WRL-420cL");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(1)).isEqualTo("53000000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(2)).isEqualTo(MeterEnums.MeterType.WRL420CL.getMeterType());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(3)).isEqualTo("53000000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(4)).isEqualTo(MeterEnums.MeterType.WRL420CL.getManufacturer().getManufacturer());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(5)).isEqualTo(MeterEnums.MeterType.WRL420CL.getModel());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(6)).isEqualTo("Enabled");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Readings Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_MeterReadings_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelCount()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(0)).isEqualTo("Usage Reading:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(1)).isEqualTo("Previous:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(2)).isEqualTo("Total Consumption:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(3)).isEqualTo("Peak Demand:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(4)).isEqualTo("Demand:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(5)).isEqualTo("Voltage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_MeterReadings_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueCount()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(0)).isEqualTo("16,694.008 kWH 08/26/2020 13:08:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(1)).isEqualTo("16,694.008 kWH 08/26/2020 13:08:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(2)).isEqualTo("0.000");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(3)).isEqualTo("2.414 kW 08/26/2020 13:14:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(4)).isEqualTo("0.166 kW 08/26/2020 13:08:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(5)).isEqualTo("242.0 Volts 08/26/2020 13:12:00");
        softly.assertAll();
    }

    // ================================================================================
    // Wi-Fi Connection Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_WiFiConnection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelByRow(0)).isEqualTo("Communication Status:");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelByRow(1)).isEqualTo("RSSI:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_WiFiConnection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueByRow(0)).isEqualTo("Connected  08/26/2020 13:13:00");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueByRow(1)).isEqualTo("-33 dBm 08/26/2020 13:13:00");
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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_Outages_LabelsCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	SoftAssertions softly = new SoftAssertions();
//    	
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelCount()).isEqualTo(3);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(0)).isEqualTo("Blink Count:");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(1)).isEqualTo("RFN Outage Count:");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(2)).isEqualTo("RFN Blink Count:");
//    	softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_Outages_ValuesCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	SoftAssertions softly = new SoftAssertions();
//    	
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueCount()).isEqualTo(3);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(0)).isEqualTo("5 Counts 08/26/2020 13:15:00");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(1)).isEqualTo("2 Counts 08/26/2020 13:15:00");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(2)).isEqualTo("7 Counts 08/26/2020 13:15:00");
//    	softly.assertAll();
    }

    // ================================================================================
    // Time of Use Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateA_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getLabelByRow(0)).isEqualTo("Usage Rate A:");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getLabelByRow(1)).isEqualTo("Peak Demand Rate A:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateB_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getLabelByRow(0)).isEqualTo("Usage Rate B:");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getLabelByRow(1)).isEqualTo("Peak Demand Rate B:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateC_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getLabelByRow(0)).isEqualTo("Usage Rate C:");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getLabelByRow(1)).isEqualTo("Peak Demand Rate C:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateD_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getLabelByRow(0)).isEqualTo("Usage Rate D:");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getLabelByRow(1)).isEqualTo("Peak Demand Rate D:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateA_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getValueByRow(0)).isEqualTo("5,727.212 kWH 08/26/2020 13:23:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getValueByRow(1)).isEqualTo("2.414 kW 08/26/2020 13:16:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateB_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueByRow(0)).isEqualTo("4,526.197 kWH 08/26/2020 13:23:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueByRow(1)).isEqualTo("2.329 kW 08/26/2020 13:16:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateC_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueByRow(0)).isEqualTo("3,360.728 kWH 08/26/2020 13:23:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueByRow(1)).isEqualTo("1.321 kW 08/26/2020 13:16:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateD_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueByRow(0)).isEqualTo("3,079.871 kWH 08/26/2020 13:23:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueByRow(1)).isEqualTo("2.113 kW 08/26/2020 13:16:00");
        softly.assertAll();
    }

    // ================================================================================
    // Device Configuration Section
    // ================================================================================

    // ================================================================================
    // Actions Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLDetail_Delete_Success() {
        setRefreshPage(true);
        String deviceId = TestDbDataType.MeterData.WRL_420CL_DELETE_ID.getId().toString();
        final String EXPECTED_MSG = "Meter AT Delete " + MeterEnums.MeterType.WRL420CL.getMeterType() + " deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + deviceId);

        WRL420cLMeterDetailsPage meterDetailsPage = new WRL420cLMeterDetailsPage(driverExt, Integer.parseInt(deviceId));

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}