package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupItronCreatePage;

public class LoadGroupItronCreateTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private LoadGroupItronCreatePage createPage;
    private static final String TYPE = "LM_GROUP_ITRON";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupItronCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_AllFieldsDisableTrue_Successful() {
        JSONObject jo = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRelay(Optional.empty())
                .build();

        String name = jo.getJSONObject(TYPE).getString("name");

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);

        waitForLoadingSpinner();

        String disableGroup = "Yes";
        if (jo.getJSONObject(TYPE).getBoolean("disableGroup")) {
            disableGroup = "Yes";
        } else { disableGroup = "No"; }
        
        String disableControl = "Yes";
        if (jo.getJSONObject(TYPE).getBoolean("disableControl")) {
            disableGroup = "Yes";
        } else { disableGroup = "No"; }
            
        createPage.getRelay().selectItemByValue(String.valueOf(jo.getJSONObject(TYPE).getInt("virtualRelayId")));
        createPage.getkWCapacity().setInputValue(String.valueOf(jo.getJSONObject(TYPE).getDouble("kWCapacity")));
        createPage.getDisableGroup().selectValue(disableGroup);
        createPage.getDisableControl().selectValue(disableControl);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_RequiredFields_SavedSuccessfully() {
        JSONObject jo = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
                .build();

        String name = jo.getJSONObject(TYPE).getString("name");

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);

        waitForLoadingSpinner();

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);

        List<String> row = detailsPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(row.get(0)).isEqualTo("1");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateItron_RelayDropDownContainsExpectedValues() {
        List<String> expectedRelayValues = new ArrayList<>(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        createPage.getType().selectItemByValue(TYPE);

        List<String> actualRelayValues = createPage.getRelay().getOptionValues();

        assertThat(expectedRelayValues).containsExactlyElementsOf(actualRelayValues);
    }
}
