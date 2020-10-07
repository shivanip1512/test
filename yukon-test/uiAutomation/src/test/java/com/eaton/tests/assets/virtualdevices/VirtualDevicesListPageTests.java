package com.eaton.tests.assets.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder.Builder;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesListPage;
import com.github.javafaker.Faker;

public class VirtualDevicesListPageTests extends SeleniumTestSetup{
    
    private VirtualDevicesListPage listPage;
    private DriverExtensions driverExt;
    private Faker faker;
    private List<String> names;
    private List<String> status;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
        Builder builder = new VirtualDeviceCreateBuilder.Builder(Optional.empty());
        String[] deviceNames = { "Virtual Device","virtual test device" , "Sample device", "test device", "test_device" };
        for(String deviceName : deviceNames) {
            builder.withName(deviceName).withEnable(Optional.of(faker.random().nextBoolean()));
            builder.create();
        }
        
        navigate(Urls.Assets.VIRTUAL_DEVICES);
        listPage = new VirtualDevicesListPage(driverExt);
        names = listPage.getTable().getDataRowsTextByCellIndex(1);
        status = listPage.getTable().getDataRowsTextByCellIndex(2);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.VIRTUAL_DEVICES })
    public void virtualDevicesList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Virtual Devices";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.VIRTUAL_DEVICES })
    public void virtualDevicesList_ColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        final int EXPECTED_COUNT = 2;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("Status");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES })
    public void virtualDevicesList_Create_OpensCorrectModal() {
        final String EXPECTED_TITLE = "Create Virtual Device";

        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        String actualModalTitle = createModal.getModalTitle();
        refreshPage(listPage);

        assertThat(actualModalTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    /*    Disabling this test as a defect is raised for incorrect sorting order (YUK-22982)*/
    @Test(enabled = false, groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void virtualDevicesList_SortNamesAsc_Correctly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.VIRTUAL_DEVICES_NAME_ASC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }
    
    /*    Disabling this test as a defect is raised for incorrect sorting order (YUK-22982)*/
    @Test(enabled = false, groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void virtualDevicesList_SortNamesDesc_Correctly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(names);

        navigate(Urls.Assets.VIRTUAL_DEVICES_NAME_DESC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }
    
    /*    Disabling this test as a defect is raised for incorrect sorting order (YUK-22982)*/    
    @Test(enabled = false, groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void virtualDevicesList_SortStatusAsc_Correctly() {
        Collections.sort(status, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.VIRTUAL_DEVICES_STATUS_ASC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(status).isEqualTo(statusList);
    }
    
/*    Disabling this test as a defect is raised for incorrect sorting order (YUK-22982)*/
    @Test(enabled = false, groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void virtualDevicesList_SortStatusDesc_Correctly() {
        Collections.sort(status, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(status);

        navigate(Urls.Assets.VIRTUAL_DEVICES_STATUS_DESC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(status).isEqualTo(statusList);
    }
}
