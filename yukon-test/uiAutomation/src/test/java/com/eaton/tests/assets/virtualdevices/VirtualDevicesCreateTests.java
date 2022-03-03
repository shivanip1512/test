package com.eaton.tests.assets.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesListPage;
import com.github.javafaker.Faker;

public class VirtualDevicesCreateTests extends SeleniumTestSetup {
    
    private VirtualDevicesListPage listPage;
    private DriverExtensions driverExt;
    private Faker faker;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Assets.VIRTUAL_DEVICES);
        faker = SeleniumTestSetup.getFaker();
        listPage = new VirtualDevicesListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(listPage);
    }    
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_Labels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        List<String> labels = createModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(3);
        softly.assertThat(labels.get(0)).isEqualTo("Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Type:");
        softly.assertThat(labels.get(2)).contains("Status:");
        softly.assertAll();
    }
    
//    To Do.... For TC "virtualDevicesCreate_Name_AlreadyExistsValidation" we need to create a Virtual Device via builder and it is not yet ready. So kept this in To Do
    
//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS })
//    public void virtualDevicesCreate_Name_AlreadyExistsValidation() {
//    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_Name_InvalidCharValidation() {
        String name = "AT Virtual Devices " + "/ \\ , ' \" |";

        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        createModal.getName().setInputValue(name);
        createModal.clickOk();

        String errorMsg = createModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_Name_MaxLength60Validation() {
        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        assertThat(createModal.getName().getMaxLength()).isEqualTo("60");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_Name_RequiredValidation() {
        final String EXPECTED_MSG = "Name is required.";

        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        createModal.clickOk();

        String errorMsg = createModal.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_AllFieldsEnabled_Success() {
    	String name = "AT Virtual Device" + faker.number().digits(10);

        final String EXPECTED_MSG = name + " saved successfully.";
        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        createModal.getName().setInputValue(name);
        createModal.getStatus().selectValue("Enabled");
        createModal.clickOk();
        
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesCreate_AllFieldsDisabled_Success() {
    	String name = "AT Virtual Device" + faker.number().digits(10);

        final String EXPECTED_MSG = name + " saved successfully.";
        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        createModal.getName().setInputValue(name);
        createModal.getStatus().selectValue("Disabled");
        createModal.clickOk();
        
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

//    //TODO: Used to create lots of virtual devices for manual testing
//    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
//    public void virtualDevicesCreate_CreateMultiple_Success() {
//        VirtualDeviceCreateService.buildAndCreateMultipleVirtualDeviceRequiredFields(1002);
//    }
    
//  //TODO: Used to create virtual devices with lots of points for manual testing 
//  @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
//  public void virtualDevicesCreate_CreateVirDevWithMultiplePoints_Success() {
//      VirtualDeviceCreateService.buildAndCreateVirtualDeviceRequiredFieldsWithMultiplePoints(170);
//  }
    
//  //TODO: Used to create virtual devices with lots of points for manual testing 
//  @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
//  public void virtualDevicesCreate_CreateVirDevWithMultiplePointsLinkedToCustomAttribute_Success() {
//      VirtualDeviceCreateService.createVirtualDeviceWithMultiplePointsAndCustomAttributes(170);
//  }
}
