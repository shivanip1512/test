package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.RFN420cLMeterDetailsPage;

public class MeterCommonEditTests extends SeleniumTestSetup {
	
    private DriverExtensions driverExt;
    private RFN420cLMeterDetailsPage meterDetailsPageWontEdit;
    
    private static final int WONT_EDIT_DEVICE_ID = 1295;
    private static final int COULD_EDIT_DEVICE_ID = 1296;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        navigate(Urls.Ami.METER_DETAIL + WONT_EDIT_DEVICE_ID);
        meterDetailsPageWontEdit = new RFN420cLMeterDetailsPage(driverExt, WONT_EDIT_DEVICE_ID);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
    	if(getRefreshPage()) {
    		refreshPage(meterDetailsPageWontEdit);
    	}
    	
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_DeviceName_RequiredValidation() {
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	RFN420cLMeterDetailsPage meterDetailsPageCouldEdit = new RFN420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);

    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();
        
    	editModal.getDeviceName().setInputValue("");

    	editModal.clickOkAndWaitForModalToClose();

        String errorMsg = editModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Device name is required.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_DeviceName_InvalidCharValidation() {
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	RFN420cLMeterDetailsPage meterDetailsPageCouldEdit = new RFN420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();

        String deviceName = "Meter / \\ , ' ";

        editModal.getDeviceName().setInputValue(deviceName);

    	editModal.clickOkAndWaitForModalToClose();

        String errorMsg = editModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_DeviceName_MaxLength60Chars() {
    	setRefreshPage(true);
    	
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();
    	
        assertThat(editModal.getDeviceName().getMaxLength()).isEqualTo("60");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_DeviceName_AlreadyExistsValidation() {
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	RFN420cLMeterDetailsPage meterDetailsPageCouldEdit = new RFN420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();

        String deviceName = "AT Detail WRL-420cL";
        
        editModal.getDeviceName().setInputValue(deviceName);

        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Device name must be unique.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_MeterNumber_RequiredValidation() {
    	setRefreshPage(true);
    	
    	navigate(Urls.Ami.METER_DETAIL + COULD_EDIT_DEVICE_ID);
    	RFN420cLMeterDetailsPage meterDetailsPageCouldEdit = new RFN420cLMeterDetailsPage(driverExt, COULD_EDIT_DEVICE_ID);
    	
    	EditMeterModal editModal = meterDetailsPageCouldEdit.showMeterEditModal();
    	editModal.getMeterNumber().setInputValue("");
    	
    	editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getMeterNumber().getValidationError();

        assertThat(errorMsg).isEqualTo("Meter number is required.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_MeterNumber_MaxLength50Chars() {
    	setRefreshPage(true);
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();
        assertThat(editModal.getMeterNumber().getMaxLength()).isEqualTo("50");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editMeter_Cancel_NavigatesToCorrectUrl() {
    	setRefreshPage(true);
    	
    	String pageTitle = meterDetailsPageWontEdit.getPageTitle();
    	
    	EditMeterModal editModal = meterDetailsPageWontEdit.showMeterEditModal();

    	editModal.clickCancelAndWait();
        
    	meterDetailsPageWontEdit = new RFN420cLMeterDetailsPage(driverExt, WONT_EDIT_DEVICE_ID);
        
        assertThat(meterDetailsPageWontEdit.getPageTitle()).isEqualTo(pageTitle);
    }
}
