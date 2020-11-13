package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ScenarioDetailPage;
import com.eaton.pages.demandresponse.ScenarioEditPage;

public class ScenarioEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Scenario: AT Scenario";
        
        String scenarioId = TestDbDataType.DemandResponseData.SCENARIO_ID.getId().toString();

        navigate(Urls.DemandResponse.SCENARIO_EDIT + scenarioId + Urls.EDIT);

        ScenarioEditPage editPage = new ScenarioEditPage(driverExt, Integer.parseInt(scenarioId));

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioEdit_RequiredFieldsOnly_Success() {
        String id = TestDbDataType.DemandResponseData.SCENARIO_EDIT_ID.getId().toString();
        navigate(Urls.DemandResponse.SCENARIO_EDIT + id + Urls.EDIT);
        
        String scenarioEditId = TestDbDataType.DemandResponseData.SCENARIO_EDIT_ID.getId().toString();

        ScenarioEditPage editPage = new ScenarioEditPage(driverExt, Integer.parseInt(scenarioEditId));

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Scenario " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";

        editPage.getName().setInputValue(name);

        editPage.getSave().click();

        waitForPageToLoad("Scenario: " + name, Optional.empty());

        ScenarioDetailPage detailsPage = new ScenarioDetailPage(driverExt, Integer.parseInt(scenarioEditId));

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
