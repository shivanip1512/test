package com.eaton.tests.assets.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesDetailPage;

public class VirtualDevicesEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private VirtualDevicesDetailPage detailPage;
    private int virtualDeviceId;
    private String virtualDeviceName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.of(true))
                .create();
        
        virtualDeviceId = pair.getValue1().getInt("id");
        virtualDeviceName = pair.getValue1().getString("name");
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId); 
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceEdit_FieldValues_Correct() {
        SoftAssertions softly = new SoftAssertions();
        // Click edit button and waits Edit virtual device modal to open
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validations for Modal Title Virtual device name and Status
        softly.assertThat(editVirtualDeviceModal.getModalTitle()).isEqualTo("Edit " + virtualDeviceName);
        softly.assertThat(editVirtualDeviceModal.getName().getInputValue()).isEqualTo(virtualDeviceName);
        softly.assertThat(editVirtualDeviceModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceEdit_AllFieldsEnabled_Success() {
        // Create disabled virtual device via API
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.of(true))
                .create();
        int id = pair.getValue1().getInt("id");
        String virtualDevice = pair.getValue1().getString("name");
        String EXPECTED_MSG = "Edit " + virtualDevice + " saved successfully.";

        // Navigate to Virtual Device page
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + id);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Change the status and Name of virtual device
        editVirtualDeviceModal.getStatus().selectValue("Enabled");
        editVirtualDeviceModal.getName().setInputValue("Edit " + virtualDevice);
        editVirtualDeviceModal.clickOkAndWaitForModalToClose();
        // Validate success message
        waitForLoadingSpinner();
        String userMsg = detailPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceEdit_AllFieldsDisabled_Success() {
        // Create disabled virtual device via API
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.of(false))
                .create();
        int id = pair.getValue1().getInt("id");
        String virtualDevice = pair.getValue1().getString("name");
        String EXPECTED_MSG = "Edit " + virtualDevice + " saved successfully.";
        // Navigate to Virtual Device page
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + id);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Change the status and Name of virtual device and
        editVirtualDeviceModal.getStatus().selectValue("Disabled");
        editVirtualDeviceModal.getName().setInputValue("Edit " + virtualDevice);
        editVirtualDeviceModal.clickOkAndWaitForModalToClose();
        // Validate success message
        waitForLoadingSpinner();
        String userMsg = detailPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesEdit_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validate the Labels are correct
        List<String> labels = editVirtualDeviceModal.getFieldLabels();
        softly.assertThat(labels.size()).isEqualTo(3);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Type:");
        softly.assertThat(labels.get(2)).contains("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesEdit_Name_RequiredValidation() {
        final String EXPECTED_MSG = "Name is required.";
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        editVirtualDeviceModal.getName().setInputValue("");
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesEdit_Name_InvalidCharValidation() {
        String name = "AT Virtual Devices " + "/ \\ , ' \" |";
        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        editVirtualDeviceModal.getName().setInputValue(name);
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesEdit_Name_AlreadyExistsValidation() {
        final String EXPECTED_MSG = "Name already exists";
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.empty())
                .withEnable(Optional.of(true)).create();
        String virtualDeviceName = pair.getValue1().getString("name");
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();

        editVirtualDeviceModal.getName().setInputValue(virtualDeviceName);
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesEdit_Name_MaxLength60Validation() {
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        assertThat(editVirtualDeviceModal.getName().getMaxLength()).isEqualTo("60");
    }

}
