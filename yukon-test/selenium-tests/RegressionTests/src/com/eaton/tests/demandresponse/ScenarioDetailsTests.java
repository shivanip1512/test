package com.eaton.tests.demandresponse;

import java.util.Optional;

import org.testng.Assert;
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

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_14_DeleteScenario" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Scenario: AT Scenario";
        
        navigate(Urls.DemandResponse.SCENARIO_DETAILS + "663");
        
        ScenarioDetailPage editPage = new ScenarioDetailPage(driverExt, Urls.DemandResponse.SCENARIO_DETAILS + "663");

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }    
    
    @Test(enabled = true, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_14_DeleteScenario"})
    public void deleteScenarioSuccess() {
        final String EXPECTED_MSG = "AT Delete Scenario deleted successfully.";
        
        navigate(Urls.DemandResponse.SCENARIO_DETAILS + "619");

        ScenarioDetailPage detailPage = new ScenarioDetailPage(driverExt, Urls.DemandResponse.SCENARIO_DETAILS + "619");
        
        ConfirmModal  confirmModal = detailPage.showDeleteControlAreaModal();
        
        confirmModal.clickOkAndWait();
        
        waitForPageToLoad("Setup", Optional.empty());
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.DemandResponse.SETUP_FILTER + Urls.Filters.CONTROl_SCENARIO);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }  

}
