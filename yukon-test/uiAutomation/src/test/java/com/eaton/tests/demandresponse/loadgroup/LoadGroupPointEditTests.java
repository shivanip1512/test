package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder.Builder;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupPointEditPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.github.javafaker.Faker;

public class LoadGroupPointEditTests extends SeleniumTestSetup {

    private LoadGroupPointEditPage editPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    Builder builder;
    private Integer id;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpPointEdit_AllFields_Success() {
        builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String createName = "AT Create Point Ld group " + timeStamp;
        String editName = "AT Edit Point Ld group " + timeStamp;
        Pair<JSONObject, JSONObject> pair = new LoadGroupPointCreateBuilder.Builder(Optional.empty())
                .withName(createName)
                .withPointUsageId(Optional.of(TestDbDataType.PointData.CAPACITOR_BANK_STATE))
                .withDeviceUsageId(Optional.empty())
                .withPointStartControlRawState(Optional.of(LoadGroupEnums.PointStartControlRawState.FALSE))
                .withKwCapacity(Optional.of(67.0))
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(false))
                .create();

        final String EXPECTED_MSG = editName + " saved successfully.";
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        double capacity = faker.number().randomDouble(2, 1, 9999);

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupPointEditPage(driverExt, id);
        editPage.getName().setInputValue(editName);
        SelectPointModal pointGroupControlDevice = editPage.showAndWaitPointGroupControlDeviceModal();
        pointGroupControlDevice.selectPoint("SCADA Override", Optional.empty());
        pointGroupControlDevice.clickOkAndWaitForModalCloseDisplayNone();

        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().selectValue("No");
        editPage.getDisableControl().selectValue("Yes");
        // 1 = true
        editPage.getControlStartState().selectItemByValue("1");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
