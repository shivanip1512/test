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
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.RFN420cLMeterDetailsPage;
import com.github.javafaker.Faker;

public class RFNMeterEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private Faker faker;
    private RFN420cLMeterDetailsPage detailsPage;
    
    private static final String UPDATED = " updated successfully.";
    private static final String METER = "Meter ";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();
        
        Integer deviceId = TestDbDataType.MeterData.RFN_420CL_WONTEDIT_ID.getId();

        navigate(Urls.Ami.METER_DETAIL + deviceId);
        detailsPage = new RFN420cLMeterDetailsPage(driverExt, deviceId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailsPage);
        }
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_Labels_Correct() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        List<String> fieldLabels = editModal.getFieldLabels();
        softly.assertThat(fieldLabels.get(0)).isEqualTo("Device Name:");
        softly.assertThat(fieldLabels.get(1)).isEqualTo("Meter Number:");
        softly.assertThat(fieldLabels.get(2)).isEqualTo("Serial Number:");
        softly.assertThat(fieldLabels.get(3)).isEqualTo("Manufacturer:");
        softly.assertThat(fieldLabels.get(4)).isEqualTo("Model:");
        softly.assertThat(fieldLabels.get(5)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_RequiredOnlyFields_Success() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        Integer editDeviceId = TestDbDataType.MeterData.RFN_420CL_WILLEDIT_ID.getId();
        navigate(Urls.Ami.METER_DETAIL + editDeviceId);

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = faker.number().numberBetween(1, 999999);

        editModal.getDeviceName().setInputValue(deviceName);
        editModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        editModal.getSerialNumber().setInputValue("");
        editModal.getManufacturer().setInputValue("");
        editModal.getModel().setInputValue("");
        String status = editModal.getStatus().getCheckedValue();

        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + editDeviceId, Optional.of(10));

        String userMsg = detailsPage.getUserMessage();

        softly.assertThat(userMsg).isEqualTo(METER + deviceName + UPDATED);
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(0)).isEqualTo(deviceName);
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(1)).isEqualTo(String.valueOf(meterNumber));
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(2)).isEqualTo(MeterEnums.MeterType.RFN420CL.getMeterType());
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(3)).isEqualTo("");
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(4)).isEqualTo("");
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(5)).isEqualTo("");
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(6)).isEqualTo(status);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_AllFields_Success() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        Integer editDeviceId = TestDbDataType.MeterData.RFN_420CL_EDITALL_ID.getId();

        navigate(Urls.Ami.METER_DETAIL + editDeviceId);

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String deviceName = "AT " + MeterEnums.MeterType.RFN420CL.getMeterType() + " Meter " + timeStamp;
        int meterNumber = faker.number().numberBetween(1, 999999);
        int serialNumber = faker.number().numberBetween(1, 9999999);
        String status = "Disabled";

        editModal.getDeviceName().setInputValue(deviceName);
        editModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        editModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        editModal.getManufacturer().setInputValue(MeterEnums.MeterType.RFN420CL.getManufacturer().getManufacturer());
        editModal.getModel().setInputValue(MeterEnums.MeterType.RFN420CL.getModel());
        editModal.getStatus().selectValue(status);

        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + editDeviceId, Optional.of(10));

        String userMsg = detailsPage.getUserMessage();

        softly.assertThat(userMsg).isEqualTo(METER + deviceName + UPDATED);
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(0)).isEqualTo(deviceName);
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(1)).isEqualTo(String.valueOf(meterNumber));
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(2)).isEqualTo(MeterEnums.MeterType.RFN420CL.getMeterType());
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(3)).isEqualTo(String.valueOf(serialNumber));
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(4)).isEqualTo(MeterEnums.MeterType.RFN420CL.getManufacturer().getManufacturer());
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(5)).isEqualTo(MeterEnums.MeterType.RFN420CL.getModel());
        softly.assertThat(detailsPage.getMeterInfoPanel().getTable().getValueByRow(6)).isEqualTo(status);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_SerialNumber_MaxLength30Chars() {
        // The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length
        // after a form submission
        setRefreshPage(true);

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        String serialNumber = faker.number().digits(31);
                
        editModal.getSerialNumber().setInputValue(serialNumber);

        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getSerialNumber().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 30.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_Manufacturer_MaxLength60Chars() {
        // The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length
        // after a form submission
        setRefreshPage(true);

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        String manufacturer = faker.lorem().characters(61);

        editModal.getManufacturer().setInputValue(manufacturer);

        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getManufacturer().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 60.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_Model_MaxLength60Chars() {
        // The improvement suggestion YUK-22989 was submitted to have the field use MaxLength instead of validating the length
        // after a form submission
        setRefreshPage(true);

        EditMeterModal editModal = detailsPage.showMeterEditModal();

        String model = faker.lorem().characters(61);

        editModal.getModel().setInputValue(model);

        editModal.clickOkAndWaitForSpinner();

        String errorMsg = editModal.getModel().getValidationError();

        assertThat(errorMsg).isEqualTo("Exceeds maximum length of 60.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void editRFNMeter_Check_AllFields() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        EditMeterModal editModal = detailsPage.showMeterEditModal();
        softly.assertThat(editModal.getDeviceName().getInputValue()).isEqualTo("AT Wont Edit RFN-420cL");
        softly.assertThat(editModal.getMeterNumber().getInputValue()).isEqualTo("53000003");
        softly.assertThat(editModal.getSerialNumber().getInputValue()).isEqualTo("530000030");
        softly.assertThat(editModal.getManufacturer().getInputValue()).isEqualTo("ITRN");
        softly.assertThat(editModal.getModel().getInputValue()).isEqualTo("C2SX");
        softly.assertThat(editModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();
    }
}
