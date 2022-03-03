package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.util.List;

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

public class MeterCreateCommonTests extends SeleniumTestSetup {

    private AmiDashboardPage amiDashboardPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        navigate(Urls.Ami.AMI_DASHBOARD);

        amiDashboardPage = new AmiDashboardPage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(amiDashboardPage);
        }
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_DeviceName_RequiredValidation() {
        setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Device name is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_DeviceName_InvalidCharValidation() {
        setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String deviceName = "Meter / \\ , ' ";

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_DeviceName_MaxLength60Chars() {
        setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();
        
        assertThat(createModal.getDeviceName().getMaxLength()).isEqualTo("60");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_DeviceName_AlreadyExistsValidation() {
        setRefreshPage(true);
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        String deviceName = "AT Detail WRL-420cL";

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());
        createModal.getDeviceName().setInputValue(deviceName);

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getDeviceName().getValidationError();

        assertThat(errorMsg).isEqualTo("Device name must be unique.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_MeterNumber_RequiredValidation() {
        setRefreshPage(true);
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        createModal.getType().selectItemByTextSearch(MeterEnums.MeterType.RFN420CL.getMeterType());

        createModal.clickOkAndWaitForSpinner();

        String errorMsg = createModal.getMeterNumber().getValidationError();

        assertThat(errorMsg).isEqualTo("Meter number is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_MeterNumber_MaxLength50Chars() {
        setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();
        assertThat(createModal.getMeterNumber().getMaxLength()).isEqualTo("50");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_Cancel_NavigatesToCorrectUrl() {
        setRefreshPage(true);

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        createModal.clickCancelAndWait();

        AmiDashboardPage amiPage = new AmiDashboardPage(driverExt);

        String actualPageTitle = amiPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo("Dashboard: Default AMI Dashboard");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void createMeter_Type_LabelsCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        List<String> dropDownItems = createModal.getType().getDropDownItems();
        softly.assertThat(dropDownItems.get(0)).isEqualTo("MCT-310CT");
        softly.assertThat(dropDownItems.get(1)).isEqualTo("MCT-310IDL");
        softly.assertThat(dropDownItems.get(2)).isEqualTo("MCT-310IL");
        softly.assertThat(dropDownItems.get(3)).isEqualTo("MCT-318L");
        softly.assertThat(dropDownItems.get(4)).isEqualTo("MCT-360");
        softly.assertThat(dropDownItems.get(5)).isEqualTo("MCT-370");
        softly.assertThat(dropDownItems.get(6)).isEqualTo("MCT-410cL");
        softly.assertThat(dropDownItems.get(7)).isEqualTo("MCT-410fL");
        softly.assertThat(dropDownItems.get(8)).isEqualTo("MCT-410iL");
        softly.assertThat(dropDownItems.get(9)).isEqualTo("MCT-420cD");
        softly.assertThat(dropDownItems.get(10)).isEqualTo("MCT-420cL");
        softly.assertThat(dropDownItems.get(11)).isEqualTo("MCT-420fD");
        softly.assertThat(dropDownItems.get(12)).isEqualTo("MCT-420fL");
        softly.assertThat(dropDownItems.get(13)).isEqualTo("MCT-430A");
        softly.assertThat(dropDownItems.get(14)).isEqualTo("MCT-430A3");
        softly.assertThat(dropDownItems.get(15)).isEqualTo("MCT-430S4");
        softly.assertThat(dropDownItems.get(16)).isEqualTo("MCT-430SL");
        softly.assertThat(dropDownItems.get(17)).isEqualTo("MCT-470");
        softly.assertThat(dropDownItems.get(18)).isEqualTo("RFG-201");
        softly.assertThat(dropDownItems.get(19)).isEqualTo("RFN-410cL");
        softly.assertThat(dropDownItems.get(20)).isEqualTo("RFN-410fD");
        softly.assertThat(dropDownItems.get(21)).isEqualTo("RFN-410fL");
        softly.assertThat(dropDownItems.get(22)).isEqualTo("RFN-410fX");
        softly.assertThat(dropDownItems.get(23)).isEqualTo("RFN-420cD");
        softly.assertThat(dropDownItems.get(24)).isEqualTo("RFN-420cL");
        softly.assertThat(dropDownItems.get(25)).isEqualTo("RFN-420fD");
        softly.assertThat(dropDownItems.get(26)).isEqualTo("RFN-420fL");
        softly.assertThat(dropDownItems.get(27)).isEqualTo("RFN-420fRD");
        softly.assertThat(dropDownItems.get(28)).isEqualTo("RFN-420fRX");
        softly.assertThat(dropDownItems.get(29)).isEqualTo("RFN-420fX");
        softly.assertThat(dropDownItems.get(30)).isEqualTo("RFN-430A3D");
        softly.assertThat(dropDownItems.get(31)).isEqualTo("RFN-430A3K");
        softly.assertThat(dropDownItems.get(32)).isEqualTo("RFN-430A3R");
        softly.assertThat(dropDownItems.get(33)).isEqualTo("RFN-430A3T");
        softly.assertThat(dropDownItems.get(34)).isEqualTo("RFN-430SL0");
        softly.assertThat(dropDownItems.get(35)).isEqualTo("RFN-430SL1");
        softly.assertThat(dropDownItems.get(36)).isEqualTo("RFN-430SL2");
        softly.assertThat(dropDownItems.get(37)).isEqualTo("RFN-430SL3");
        softly.assertThat(dropDownItems.get(38)).isEqualTo("RFN-430SL4");
        softly.assertThat(dropDownItems.get(39)).isEqualTo("RFN-510fL");
        softly.assertThat(dropDownItems.get(40)).isEqualTo("RFN-520fAX");
        softly.assertThat(dropDownItems.get(41)).isEqualTo("RFN-520fAXD");
        softly.assertThat(dropDownItems.get(42)).isEqualTo("RFN-520fRX");
        softly.assertThat(dropDownItems.get(43)).isEqualTo("RFN-520fRXD");
        softly.assertThat(dropDownItems.get(44)).isEqualTo("RFN-530S4eAX");
        softly.assertThat(dropDownItems.get(45)).isEqualTo("RFN-530S4eAXR");
        softly.assertThat(dropDownItems.get(46)).isEqualTo("RFN-530S4eRX");
        softly.assertThat(dropDownItems.get(47)).isEqualTo("RFN-530S4eRXR");
        softly.assertThat(dropDownItems.get(48)).isEqualTo("RFN-530S4x");
        softly.assertThat(dropDownItems.get(49)).isEqualTo("RFN-530fAX");
        softly.assertThat(dropDownItems.get(50)).isEqualTo("RFN-530fRX");
        softly.assertThat(dropDownItems.get(51)).isEqualTo("RFW-201");
        softly.assertThat(dropDownItems.get(52)).isEqualTo("RFW-Meter");
        softly.assertThat(dropDownItems.get(53)).isEqualTo("WRL-420cD");
        softly.assertThat(dropDownItems.get(54)).isEqualTo("WRL-420cL");
        softly.assertAll();
    }
}
