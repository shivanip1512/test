package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.PointId;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder.Builder;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupPointEditPage;

public class LoadGroupPointEditTests extends SeleniumTestSetup {

    private LoadGroupPointEditPage editPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;
    Builder builder;
    private Integer id;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpPointEdit_AllFields_Successfully() {
        builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String createName = "AT Create Point Ld group " + timeStamp;
        String editName = "AT Edit Point Ld group " + timeStamp;
        Pair<JSONObject, JSONObject> pair = new LoadGroupPointCreateBuilder.Builder(Optional.empty())
                .withName(createName)
                .withPointUsageId(Optional.of(PointId.Capacitor_Bank_State))
                .withDeviceUsageId(Optional.empty())
                .withPointStartControlRawState(Optional.of(LoadGroupEnums.PointStartControlRawState.False))
                .withKwCapacity(Optional.of(67.0))
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(false))
                .create();

        final String EXPECTED_MSG = editName + " saved successfully.";
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupPointEditPage(driverExt, id);
        editPage.getName().setInputValue(editName);
        SelectPointModal pointGroupControlDevice = editPage.showAndWaitPointGroupControlDeviceModal();
        pointGroupControlDevice.selectPoint("SCADA Override", Optional.empty());
        pointGroupControlDevice.clickOkAndWaitForModalToClose();
        editPage.getControlStartState().selectItemByText("True");

        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().setValue(false);
        editPage.getDisableControl().setValue(true);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
