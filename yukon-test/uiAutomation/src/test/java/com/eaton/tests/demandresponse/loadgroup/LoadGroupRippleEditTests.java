package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupRippleEditPage;
import com.github.javafaker.Faker;

public class LoadGroupRippleEditTests extends SeleniumTestSetup {

    private LoadGroupRippleEditPage editPage;
    private DriverExtensions driverExt;
    private Builder builder;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRippleEdit_AllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Ripple " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        final String EXPECTED_MSG = editName + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        Integer id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupRippleEditPage(driverExt, id);

        editPage.getName().setInputValue(editName);
        String commRoute = TestDbDataType.CommunicationRouteData.ARTC.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);
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
