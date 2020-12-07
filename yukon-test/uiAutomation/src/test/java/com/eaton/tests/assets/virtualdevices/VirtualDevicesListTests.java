package com.eaton.tests.assets.virtualdevices;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder;
import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateBuilder.Builder;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesListPage;
import com.github.javafaker.Faker;

public class VirtualDevicesListTests extends SeleniumTestSetup{
    
    private VirtualDevicesListPage listPage;
    private DriverExtensions driverExt;
    private Faker faker;
    private List<String> names;
    private List<String> status;
    private String devName;
    private Integer devId;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
        setRefreshPage(false);
        Builder builder = new VirtualDeviceCreateBuilder.Builder(Optional.empty());
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String[] deviceNames = { "Virtual Device" + timeStamp,"Virtual test device" + timeStamp, "Sample device" + timeStamp, "Test device" + timeStamp, "Zest device" + timeStamp};
        for(String deviceName : deviceNames) {
            builder.withName(deviceName).withEnable(Optional.of(faker.random().nextBoolean()));
            Pair<JSONObject, JSONObject> pair = builder.create();
            devId = pair.getValue1().getInt("id");
            devName = pair.getValue1().getString("name");
        }
        
        navigate(Urls.Assets.VIRTUAL_DEVICES);
        listPage = new VirtualDevicesListPage(driverExt);
        names = listPage.getTable().getDataRowsTextByCellIndex(1);
        status = listPage.getTable().getDataRowsTextByCellIndex(2);
    }
    
    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(listPage);    
        }
        setRefreshPage(false);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS  })
    public void virtualDevicesList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Virtual Devices";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS  })
    public void virtualDevicesList_ColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        final int EXPECTED_COUNT = 3;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("Meter Number");
        softly.assertThat(headers).contains("Status");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS})
    public void virtualDevicesList_Create_OpensCorrectModal() {
        final String EXPECTED_TITLE = "Create Virtual Device";
        setRefreshPage(true);

        CreateVirtualDeviceModal createModal = listPage.showAndWaitCreateVirtualDeviceModal();
        String actualModalTitle = createModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesList_SortNamesAsc_Correctly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.VIRTUAL_DEVICES_NAME_ASC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesList_SortNamesDesc_Correctly() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(names);

        navigate(Urls.Assets.VIRTUAL_DEVICES_NAME_DESC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(names).isEqualTo(namesList);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesList_SortStatusAsc_Correctly() {
        Collections.sort(status, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(status);

        navigate(Urls.Assets.VIRTUAL_DEVICES_STATUS_ASC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(status).isEqualTo(statusList);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesList_SortStatusDesc_Correctly() {
        Collections.sort(status, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.Assets.VIRTUAL_DEVICES_STATUS_DESC);

        List<String> statusList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(status).isEqualTo(statusList);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDevicesList_NameLink_Correct() {
        WebTableRow row = listPage.getTable().getDataRowByLinkName(devName);
        String link = row.getCellLinkByIndex(0);

        assertThat(link).contains(Urls.Assets.VIRTUAL_DEVICES_EDIT.concat("/" +devId.toString()));

    }
}
