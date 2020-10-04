package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class RFNMeterCreateTests extends SeleniumTestSetup {

    private AmiDashboardPage amiDashboardPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    private static final String CREATED = " created successfully.";
    private static final String METER = "Meter ";

    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.Ami.AMI_DASHBOARD);

        amiDashboardPage = new AmiDashboardPage(driverExt);
        randomNum = getRandomNum();
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
    	if(getRefreshPage()) {
    		refreshPage(amiDashboardPage);
    	}
    	setRefreshPage(false);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createRFNMeter_Labels_Correct() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());

        List<String> fieldLabels = createModal.getFieldLabels();
        softly.assertThat(fieldLabels.get(0)).isEqualTo("Type:");
        softly.assertThat(fieldLabels.get(1)).isEqualTo("Device Name:");
        softly.assertThat(fieldLabels.get(2)).isEqualTo("Meter Number:");
        softly.assertThat(fieldLabels.get(6)).isEqualTo("Serial Number:");
        softly.assertThat(fieldLabels.get(7)).isEqualTo("Manufacturer:");
        softly.assertThat(fieldLabels.get(8)).isEqualTo("Model:");
        softly.assertThat(fieldLabels.get(9)).isEqualTo("Status:");
        softly.assertAll();

    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createRFNMeter_RequiredOnlyFields_Success() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + deviceName + CREATED);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createRFNMeter_AllFields_Success() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(9999999);
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(MeterEnums.MeterType.RFN420CL.getManufacturer().getManufacturer());
        createModal.getModel().setInputValue(MeterEnums.MeterType.RFN420CL.getModel());

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + deviceName + CREATED);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMeter_SerialNumber_InvalidLengthValidation() {
    	//The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length after a form submission
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        String serialNumber = "3619944661838896601546506928503";
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(serialNumber);
        createModal.getManufacturer().setInputValue(MeterEnums.MeterType.RFN420CL.getManufacturer().getManufacturer());
        createModal.getModel().setInputValue(MeterEnums.MeterType.RFN420CL.getModel());

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getSerialNumber().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 30.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMeter_Manufacturer_InvalidLengthValidation() {
    	//The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length after a form submission
    	setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(9999999);
        String manufacturer = "Itron OpenWay Electricity Smart Meter with Two-way Communications";
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(manufacturer);
        createModal.getModel().setInputValue(MeterEnums.MeterType.RFN420CL.getModel());

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getManufacturer().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 60.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMeter_Model_InvalidLengthValidation() {
    	//The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length after a form submission
    	setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(9999999);
        String model = "Single-Phase Residential Electricity Meter CENTRON (C2SX) Meter";
        
        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(MeterEnums.MeterType.RFN420CL.getManufacturer().getManufacturer());
        createModal.getModel().setInputValue(model);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getModel().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 60.");
    }
    
}
