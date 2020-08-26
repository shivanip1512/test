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

import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupRippleEditPage;

public class LoadGroupRippleEditTests extends SeleniumTestSetup {

    private LoadGroupRippleEditPage editPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;
    Builder builder;
    private Integer id;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
        editPage = new LoadGroupRippleEditPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRippleEdit_AllFields_Successfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Ripple " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        final String EXPECTED_MSG = editName + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage.getName().setInputValue(editName);
        editPage.getCommunicationRoute().selectItemByText("a_RTC");

        editPage.getShedTime().selectItemByText("30 minutes");
        editPage.getGroup().selectItemByText("2.01");
        editPage.getAreaCode().selectItemByText("Minnkota");
        editPage.getControlSwitchElement().setTrueFalseByBitNo(10, true);
        editPage.getRestoreSwitchElement().setTrueFalseByBitNo(18, true);
        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().setValue(true);
        editPage.getDisableControl().setValue(false);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
