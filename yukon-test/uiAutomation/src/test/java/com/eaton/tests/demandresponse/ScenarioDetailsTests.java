package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.ScenarioDetailPage;

public class ScenarioDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Scenario: AT Scenario";
        
        String scenarioId = TestDbDataType.DemandResponseData.SCENARIO_ID.getId().toString();

        navigate(Urls.DemandResponse.SCENARIO_DETAILS + scenarioId);

        ScenarioDetailPage editPage = new ScenarioDetailPage(driverExt, Integer.parseInt(scenarioId));

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioDetails_Delete_Success() {
        final String EXPECTED_MSG = "AT Delete Scenario deleted successfully.";
        
        String scenarioDeleteId = TestDbDataType.DemandResponseData.SCENARIO_DELETE_ID.getId().toString();

        navigate(Urls.DemandResponse.SCENARIO_DETAILS + scenarioDeleteId);

        ScenarioDetailPage detailPage = new ScenarioDetailPage(driverExt, Integer.parseInt(scenarioDeleteId));

        ConfirmModal confirmModal = detailPage.showDeleteControlAreaModal();

        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());

        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.CONTROL_SCENARIO);

        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
