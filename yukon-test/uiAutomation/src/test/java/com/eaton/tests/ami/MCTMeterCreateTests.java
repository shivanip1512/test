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

public class MCTMeterCreateTests extends SeleniumTestSetup {

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
    public void createMCTMeter_Labels_Correct() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        List<String> fieldLabels = createModal.getFieldLabels();
        softly.assertThat(fieldLabels.get(0)).isEqualTo("Type:");
        softly.assertThat(fieldLabels.get(1)).isEqualTo("Device Name:");
        softly.assertThat(fieldLabels.get(2)).isEqualTo("Meter Number:");
        softly.assertThat(fieldLabels.get(3)).isEqualTo("Physical Address:");
        softly.assertThat(fieldLabels.get(5)).isEqualTo("Route:");
        softly.assertThat(fieldLabels.get(9)).isEqualTo("Status:");
        softly.assertAll();

    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Ami.AMI })
    public void createMCTMeter_AllFields_Success() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.MCT420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        int physicalAddress = randomNum.nextInt(4194304);

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.MCT420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getPhysicalAddress().setInputValue(String.valueOf(physicalAddress));

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + deviceName + CREATED);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMCTMeter_PhysicalAddress_InvalidValidation() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        String physicalAddress = "41 Charles St.";

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.MCT420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getPhysicalAddress().setInputValue(physicalAddress);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Must be a valid integer value.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMCTMeter_PhysicalAddress_MaxValueValidation() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);
        int physicalAddress = 4194304;

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.MCT420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getPhysicalAddress().setInputValue(String.valueOf(physicalAddress));
        
        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Physical address must be within range(s): [0 - 4194303].");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMCTMeter_PhysicalAddress_RequiredValidation() {
    	setRefreshPage(true);
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = randomNum.nextInt(999999);

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.MCT420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Physical address is required.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void createMCTMeter_Route_LabelsCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        List<String> optionValues = createModal.getRoute().getOptionValues();
        softly.assertThat(optionValues).contains("a_CCU-710A");
        softly.assertThat(optionValues).contains("a_CCU-721");
        softly.assertThat(optionValues).contains("a_LCU-EASTRIVER");
        softly.assertThat(optionValues).contains("a_PAGING TAP TERMINAL");
        softly.assertThat(optionValues).contains("a_REPEATER-800");
        softly.assertThat(optionValues).contains("a_REPEATER-801");
        softly.assertThat(optionValues).contains("a_REPEATER-900");
        softly.assertThat(optionValues).contains("a_REPEATER-902");
        softly.assertThat(optionValues).contains("a_REPEATER-921");
        softly.assertThat(optionValues).contains("a_RTC");
        softly.assertThat(optionValues).contains("a_RTU-LMI");
        softly.assertThat(optionValues).contains("a_SNPP-TERMINAL");
        softly.assertThat(optionValues).contains("a_TCU-5000");
        softly.assertThat(optionValues).contains("a_TCU-5500");
        softly.assertThat(optionValues).contains("a_WCTP-TERMINAL");
        softly.assertThat(optionValues).contains("a_XML");
        softly.assertAll();
        
    }
    
}
