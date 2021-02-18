package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupItronCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupItronEditPage;

public class LoadGroupItronEditTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private String name;
    private LoadGroupItronEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        Pair<JSONObject, JSONObject> pair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRelay(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();
        Integer id = response.getInt("id");
        name = response.getString("name");
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupItronEditPage(driverExt, id);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpItronEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpItronEdit_AllFields_Success() {
        setRefreshPage(true);
        String u = UUID.randomUUID().toString();
        String uuid = u.replace("-", "");
        String name = "AT Edit" + uuid;
        Integer relay;
        Double kwCapacity;

        final String EXPECTED_MSG = name + " saved successfully.";

        Pair<JSONObject, JSONObject> pair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRelay(Optional.empty())
                .withDisableControl(Optional.of(false))
                .withDisableGroup(Optional.of(false))
                .create();

        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");
        relay = response.getInt("virtualRelayId");

        kwCapacity = response.getDouble("kWCapacity");
        kwCapacity = kwCapacity + 1.0;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);

        editPage.getName().setInputValue(name);

        // Relay value should be between 1 to 8 only, getting relay from API and updating it
        if (relay == 1)
            relay = relay + 1;
        else
            relay = relay - 1;

        editPage.getRelay().selectItemByValue(relay.toString());
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
