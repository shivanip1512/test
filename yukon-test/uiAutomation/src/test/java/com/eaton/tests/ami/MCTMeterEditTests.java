package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MCT420cLMeterDetailsPage;
import com.github.javafaker.Faker;

public class MCTMeterEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private Faker faker;
    private MCT420cLMeterDetailsPage meterDetailsPageWontEdit;
   
    private static final int WONT_EDIT_DEVICE_ID = 1292;
    private static final int COULD_EDIT_DEVICE_ID = 1293;
    private static final int WILL_EDIT_DEVICE_ID = 1294;
    
    private static final String UPDATED = " updated successfully.";
    private static final String METER = "Meter ";
    

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();
        
        navigate(Urls.Ami.METER_DETAIL + WONT_EDIT_DEVICE_ID);

        meterDetailsPageWontEdit = new MCT420cLMeterDetailsPage(driverExt, WONT_EDIT_DEVICE_ID);
        
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
    	if(getRefreshPage()) {
    		refreshPage(meterDetailsPageWontEdit);
    	}
    	
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_Labels_Correct() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();
        
        List<String> fieldLabels = editModal.getFieldLabels();
        softly.assertThat(fieldLabels.get(0)).isEqualTo("Device Name:");
        softly.assertThat(fieldLabels.get(1)).isEqualTo("Meter Number:");
        softly.assertThat(fieldLabels.get(2)).isEqualTo("Physical Address:");
        softly.assertThat(fieldLabels.get(3)).isEqualTo("Route:");
        softly.assertThat(fieldLabels.get(4)).isEqualTo("Status:");
        softly.assertAll();

    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_AllFields_Success() {
    	setRefreshPage(true);
    	
    	SoftAssertions softly = new SoftAssertions();
    	
    	navigate(Urls.Ami.METER_DETAIL + WILL_EDIT_DEVICE_ID);
    	MCT420cLMeterDetailsPage meterDetailsPageWillEdit = new MCT420cLMeterDetailsPage(driverExt, WILL_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageWillEdit.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.MCT420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = faker.number().numberBetween(1, 999999);
        int physicalAddress = faker.number().numberBetween(1, 4194304);
        String status = "Disabled";
        
        editModal.getDeviceName().setInputValue(deviceName);
        editModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        editModal.getPhysicalAddress().setInputValue(String.valueOf(physicalAddress));
        editModal.getRoute().selectItemByIndex(2);
        editModal.getStatus().selectValue(status);
        
        String route = editModal.getRoute().getSelectedValue();

        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + WILL_EDIT_DEVICE_ID, Optional.of(10));

        meterDetailsPageWillEdit = new MCT420cLMeterDetailsPage(driverExt, WILL_EDIT_DEVICE_ID);

        String userMsg = meterDetailsPageWillEdit.getUserMessage();

        softly.assertThat(userMsg).isEqualTo(METER + deviceName + UPDATED);
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(0)).isEqualTo(deviceName);
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(1)).isEqualTo(String.valueOf(meterNumber));
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(2)).isEqualTo(MeterEnums.MeterType.MCT420CL.getMeterType());
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(3)).isEqualTo(String.valueOf(physicalAddress));
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(4)).isEqualTo(route);
        softly.assertThat(meterDetailsPageWillEdit.getMeterInfoPanel().getTable().getValueByRow(5)).isEqualTo(status);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_PhysicalAddress_InvalidValidation() {
    	
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	MCT420cLMeterDetailsPage meterDetailsPageCouldEdit = new MCT420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();
    	  
        String physicalAddress = "41 Charles St.";

        editModal.getPhysicalAddress().setInputValue(physicalAddress);

        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Must be a valid integer value.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_PhysicalAddress_MaxValueValidation() {
    	//The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length after a form submission
    	setRefreshPage(true);

    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	MCT420cLMeterDetailsPage meterDetailsPageCouldEdit = new MCT420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();

        int physicalAddress = 4194304;

        
        editModal.getPhysicalAddress().setInputValue(String.valueOf(physicalAddress));
        
        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Physical address must be within range(s): [0 - 4194303].");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_PhysicalAddress_RequiredValidation() {
    	//The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length after a form submission
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	MCT420cLMeterDetailsPage meterDetailsPageCouldEdit = new MCT420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);

    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();
    	
    	editModal.getPhysicalAddress().setInputValue("");

    	editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getPhysicalAddress().getValidationError();

        assertThat(errorMsg).isEqualTo("Physical address is required.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_Route_LabelsCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();
    	List<String> optionValues = editModal.getRoute().getOptionValues();
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
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void editMCTMeter_Check_AllFields() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();
    	softly.assertThat(editModal.getDeviceName().getInputValue()).isEqualTo("AT Wont Edit MCT-420cL");
    	softly.assertThat(editModal.getMeterNumber().getInputValue()).isEqualTo("1300000");
    	softly.assertThat(editModal.getPhysicalAddress().getInputValue()).isEqualTo("120000");
    	softly.assertThat(editModal.getRoute().getSelectedValue()).isEqualTo("a_CCU-710A");
    	softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
    	softly.assertAll();
    }
    
}
