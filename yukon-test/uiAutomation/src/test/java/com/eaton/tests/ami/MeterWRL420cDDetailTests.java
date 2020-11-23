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
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.WRL420cDMeterDetailsPage;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;

public class MeterWRL420cDDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private WRL420cDMeterDetailsPage meterDetailsPage;
    private String deviceId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        deviceId = TestDbDataType.MeterData.WRL_420CD_DETAIL_ID.getId().toString();

        setRefreshPage(false);
        navigate(Urls.Ami.METER_DETAIL + deviceId);
        meterDetailsPage = new WRL420cDMeterDetailsPage(driverExt, Integer.parseInt(deviceId));        
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(meterDetailsPage);
        }
        
        setRefreshPage(false);
    }

    // ================================================================================
    // Overall Page Section
    // ================================================================================

    @Test(groups = {TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWrl420clDetail_Panels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getPanelList().getPanelCount()).isEqualTo(12);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getPanelName()).isEqualTo("Meter Information");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getPanelName()).isEqualTo("Meter Readings");
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getPanelName()).isEqualTo("Wi-Fi Connection");
        softly.assertThat(meterDetailsPage.getNetworkInfoPanel().getPanelName()).isEqualTo("Network Information");
        softly.assertThat(meterDetailsPage.getNotesPanel().getPanelName()).isEqualTo("Notes");
        softly.assertThat(meterDetailsPage.getDeviceGroupsPanel().getPanelName()).isEqualTo("Device Groups");
        softly.assertThat(meterDetailsPage.getMeterTrendPanel().getPanelName()).isEqualTo("Meter Trend");
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getPanelName()).isEqualTo("Disconnect");
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
    public void meterWRL420cDDetail_MeterInfo_LabelsCorrect() {
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
    public void meterWRL420cDDetail_MeterInfo_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueCount()).isEqualTo(7);
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(0)).isEqualTo("AT Detail WRL-420cD");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(1)).isEqualTo("53100000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(2)).isEqualTo(MeterEnums.MeterType.WRL420CD.getMeterType());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(3)).isEqualTo("53100000");
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(4)).isEqualTo(MeterEnums.MeterType.WRL420CD.getManufacturer().getManufacturer());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(5)).isEqualTo(MeterEnums.MeterType.WRL420CD.getModel());
        softly.assertThat(meterDetailsPage.getMeterInfoPanel().getTable().getValueByRow(6)).isEqualTo("Enabled");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Readings Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_MeterReadings_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueCount()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(0)).isEqualTo("Usage Reading:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(1)).isEqualTo("Previous:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(2)).isEqualTo("Total Consumption:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(3)).isEqualTo("Peak Demand:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(4)).isEqualTo("Demand:");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getLabelByRow(5)).isEqualTo("Voltage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_MeterReadings_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueCount()).isEqualTo(6);
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(0)).isEqualTo("16,717.610 kWH 08/26/2020 13:40:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getPreviousSelectedValue()).isEqualTo("16,717.610 kWH 08/26/2020 13:40:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(2)).isEqualTo("0.000");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(3)).isEqualTo("3.245 kW 08/26/2020 13:41:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(4)).isEqualTo("0.166 kW 08/26/2020 13:41:00");
        softly.assertThat(meterDetailsPage.getMeterReadingsPanel().getTable().getValueByRow(5)).isEqualTo("243.4 Volts 08/26/2020 13:41:00");
        softly.assertAll();
    }

    // ================================================================================
    // Wi-Fi Connection Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_WiFiConnection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelByRow(0)).isEqualTo("Communication Status:");        
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getLabelByRow(1)).isEqualTo("RSSI:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_WiFiConnection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueByRow(0)).isEqualTo("Connected  08/26/2020 13:42:00");  
        softly.assertThat(meterDetailsPage.getWiFiConnectionPanel().getTable().getValueByRow(1)).isEqualTo("-43 dBm 08/26/2020 13:42:00");  
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

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_Disconnect_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getTable().getLabelCount()).isEqualTo(1);
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getTable().getLabelByRow(0)).isEqualTo("Disconnect Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_Disconnect_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getTable().getValueCount()).isEqualTo(1);
        softly.assertThat(meterDetailsPage.getDisconnectPanel().getTable().getValueByRow(0)).isEqualTo("Connected  08/26/2020 13:43:00");
        softly.assertAll();
    }

    // ================================================================================
    // Meter Events Section
    // ================================================================================

    // ================================================================================
    // Outages Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_Outages_LabelsCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	SoftAssertions softly = new SoftAssertions();
//
//    	  softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelCount()).isEqualTo(3);
//        softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(0)).isEqualTo("RFN Blink Count:");
//        softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(1)).isEqualTo("Blink Count:");
//        softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getLabelByRow(2)).isEqualTo("RFN Outage Count:");
//        
//    	softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_Outages_ValuesCorrect() {
        throw new SkipException("Development Defect: YUK-22819");
//    	SoftAssertions softly = new SoftAssertions();
//    	
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueCount()).isEqualTo(3);
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(0)).isEqualTo("8 Counts 08/26/2020 13:44:00");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(1)).isEqualTo("3 Counts 08/26/2020 13:57:00");
//    	softly.assertThat(meterDetailsPage.getOutagesPanel().getOutageTable().getValueByRow(2)).isEqualTo("11 Counts 08/26/2020 13:57:00");
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
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getValueByRow(0)).isEqualTo("4,682.120 kWH 08/26/2020 13:54:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateATable().getValueByRow(1)).isEqualTo("3.245 kW 08/26/2020 13:46:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateB_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueByRow(0)).isEqualTo("3,682.360 kWH 08/26/2020 13:54:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateBTable().getValueByRow(1)).isEqualTo("2.537 kW 08/26/2020 13:46:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateC_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueByRow(0)).isEqualTo("4,527.000 kWH 08/26/2020 13:54:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateCTable().getValueByRow(1)).isEqualTo("3.127 kW 08/26/2020 13:46:00");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_TimeOfUseUsageRateD_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueCount()).isEqualTo(2);
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueByRow(0)).isEqualTo("3,826.130 kWH 08/26/2020 13:54:00");
        softly.assertThat(meterDetailsPage.getTimeOfUsePanel().getUsageRateDTable().getValueByRow(1)).isEqualTo("2.845 kW 08/26/2020 13:46:00");
        softly.assertAll();
    }

    // ================================================================================
    // Device Configuration Section
    // ================================================================================

    // ================================================================================
    // Actions Section
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cDDetail_Delete_Success() {
        setRefreshPage(true);
        
        String deviceId = TestDbDataType.MeterData.WRL_420CD_DELETE_ID.getId().toString();
        final String EXPECTED_MSG = "Meter AT Delete " + MeterEnums.MeterType.WRL420CD.getMeterType() + " deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + deviceId);

        WRL420cDMeterDetailsPage meterDetailsPage = new WRL420cDMeterDetailsPage(driverExt, Integer.parseInt(deviceId));

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
