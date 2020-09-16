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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupRippleEditPage;

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
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRippleEdit_AllFields_Success() {
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
        editPage = new LoadGroupRippleEditPage(driverExt, id);

        editPage.getName().setInputValue(editName);
        // 62 = a_RTC
        editPage.getCommunicationRoute().selectItemByValue("62");
        // 1800 = 30 minutes
        editPage.getShedTime().selectItemByValue("1800");
        // TWO_01 = 2.01
        editPage.getGroup().selectItemByValue("TWO_01");
        editPage.getAreaCode().selectItemByValue("MINNKOTA");
        editPage.getControlSwitchElement().setTrueFalseByBitNo(10, true);
        editPage.getRestoreSwitchElement().setTrueFalseByBitNo(18, true);
        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("No");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
