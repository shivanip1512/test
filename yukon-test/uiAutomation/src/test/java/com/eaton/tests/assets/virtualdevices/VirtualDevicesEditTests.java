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
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesDetailPage;
import com.github.javafaker.Faker;

public class VirtualDevicesEditTests extends SeleniumTestSetup {
    private VirtualDevicesDetailPage detailPage;
    private DriverExtensions driverExt;
    private Faker faker;
    private int virtualDeviceId;
    private String virtualDeviceName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
       
        faker = SeleniumTestSetup.getFaker();
        virtualDeviceName = "AT Virtual Device" + faker.number().digits(10);
        navigate(Urls.Assets.VIRTUAL_DEVICES);
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.of(virtualDeviceName))
                .withEnable(Optional.of(true)).create();
        virtualDeviceId = (Integer) pair.getValue1().get("id");

        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, virtualDeviceId);

    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(detailPage);
    }
    
  

    @Test
    public void virtualDeviceEdit_FieldValues_Correct() {
        SoftAssertions softly = new SoftAssertions();
        // Navigate to virtual device detail page
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        // Click edit button and waits Edit virtual device modal to open
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validations for Modal Title Virtual device name and Status
        softly.assertThat(editVirtualDeviceModal.getModalTitle()).isEqualTo("Edit " + virtualDeviceName);
        softly.assertThat(editVirtualDeviceModal.getName().getInputValue()).isEqualTo(virtualDeviceName);
        softly.assertThat(editVirtualDeviceModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        softly.assertAll();
    }

    @Test
    public void virtualDeviceEdit_AllFieldsEnabled_Success() {
        SoftAssertions softly = new SoftAssertions();
        String virtualDevice = "AT Virtual Device" + faker.number().digits(10);
        String EXPECTED_MSG = "Edit " + virtualDevice + " saved successfully.";
        // Create disabled virtual device via API
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.of(virtualDevice))
                .withEnable(Optional.of(false)).create();
        int id = (Integer) pair.getValue1().get("id");
        // Navigate to Virtual Device page
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + id);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validate that virtual device has the status as disabled
        softly.assertThat(editVirtualDeviceModal.getStatus().getCheckedValue()).isEqualTo("Disabled");
        // Change the status and Name of virtual device and
        editVirtualDeviceModal.getStatus().selectValue("Enabled");
        editVirtualDeviceModal.getName().setInputValue("Edit " + virtualDevice);
        editVirtualDeviceModal.clickOkAndWaitForModalToClose();
        // Validate success message
        String userMsg = detailPage.getUserMessage();
        softly.assertThat(userMsg).isEqualTo(EXPECTED_MSG);
        softly.assertAll();
    }

    @Test
    public void virtualDeviceEdit_AllFieldsDisabled_Success() {
        SoftAssertions softly = new SoftAssertions();
        String virtualDevice = "AT Virtual Device" + faker.number().digits(10);
        String EXPECTED_MSG = "Edit " + virtualDevice + " saved successfully.";
        // Create disabled virtual device via API
        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.of(virtualDevice))
                .withEnable(Optional.of(true)).create();
        int id = (Integer) pair.getValue1().get("id");
        // Navigate to Virtual Device page
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + id);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validate that virtual device has the status as Enabled
        softly.assertThat(editVirtualDeviceModal.getStatus().getCheckedValue()).isEqualTo("Enabled");
        // Change the status and Name of virtual device and
        editVirtualDeviceModal.getStatus().selectValue("Disabled");
        editVirtualDeviceModal.getName().setInputValue("Edit " + virtualDevice);
        editVirtualDeviceModal.clickOkAndWaitForModalToClose();
        // Validate success message
        String userMsg = detailPage.getUserMessage();
        softly.assertThat(userMsg).isEqualTo(EXPECTED_MSG);
        softly.assertAll();
    }

    @Test
    public void virtualDevicesEdit_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        // Navigate to Virtual Device edit modal
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        // Validate the Labels are correct
        List<String> labels = editVirtualDeviceModal.getFieldLabels();
        softly.assertThat(labels.size()).isEqualTo(2);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).contains("Status:");
        softly.assertAll();
    }

    @Test
    public void virtualDevicesEdit_Name_RequiredValidation() {
        final String EXPECTED_MSG = "Name is required.";
        // Navigate to Virtual Device edit modal
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        editVirtualDeviceModal.getName().setInputValue("");
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test
    public void virtualDevicesEdit_Name_InvalidCharValidation() {
        String name = "AT Virtual Devices " + "/ \\ , ' \" |";
        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();

        editVirtualDeviceModal.getName().setInputValue(name);
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test
    public void virtualDevicesEdit_Name_AlreadyExistsValidation() {
        final String EXPECTED_MSG = "Name already exists";
        String virtualDevice = "AT Virtual Device" + faker.number().digits(10);

        Pair<JSONObject, JSONObject> pair = new VirtualDeviceCreateBuilder.Builder(Optional.of(virtualDevice))
                .withEnable(Optional.of(true)).create();
        String virtualDeviceName = (String) pair.getValue1().get("name");
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();

        editVirtualDeviceModal.getName().setInputValue(virtualDeviceName);
        editVirtualDeviceModal.clickOk();
        String errorMsg = editVirtualDeviceModal.getName().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test
    public void virtualDevicesEdit_Name_MaxLength60Validation() {
        navigate(Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceId);
        EditVirtualDeviceModal editVirtualDeviceModal = detailPage.showAndWaitEditVirtualDeviceModal();
        assertThat(editVirtualDeviceModal.getName().getMaxLength()).isEqualTo("60");
    }

}
