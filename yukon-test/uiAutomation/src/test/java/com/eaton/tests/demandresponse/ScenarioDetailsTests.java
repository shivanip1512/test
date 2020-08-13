package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.ScenarioDetailPage;

public class ScenarioDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void scenarioDetails_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Scenario: AT Scenario";

        navigate(Urls.DemandResponse.SCENARIO_DETAILS + "663");

        ScenarioDetailPage editPage = new ScenarioDetailPage(driverExt, 663);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void scenarioDetails_deleteScenarioSuccess() {
        final String EXPECTED_MSG = "AT Delete Scenario deleted successfully.";

        navigate(Urls.DemandResponse.SCENARIO_DETAILS + "619");

        ScenarioDetailPage detailPage = new ScenarioDetailPage(driverExt, 619);

        ConfirmModal confirmModal = detailPage.showDeleteControlAreaModal();

        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());

        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.CONTROL_SCENARIO);

        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
