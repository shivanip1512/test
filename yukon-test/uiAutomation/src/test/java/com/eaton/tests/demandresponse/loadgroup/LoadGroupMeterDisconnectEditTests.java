package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupMeterDisconnectCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupMeterDisconnectEditPage;

public class LoadGroupMeterDisconnectEditTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupMeterDisconnectEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        Pair<JSONObject, JSONObject> pair = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupMeterDisconnectEditPage(driverExt, id);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpMeterDisconnectEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpMeterDisconnectEdit_AllFields_Success() {
        setRefreshPage(true);
        String u = UUID.randomUUID().toString();
        String uuid = u.replace("-", "");
        String name = "AT Edit" + uuid;
        Double kwCapacity;

        final String EXPECTED_MSG = name + " saved successfully.";

        Pair<JSONObject, JSONObject> pair = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        kwCapacity = response.getDouble("kWCapacity");
        kwCapacity = kwCapacity + 1.0;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getName().setInputValue(name);
        editPage.getkWCapacity().setInputValue(kwCapacity.toString());
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");
        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
