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
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;

public class ControlAreaDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_11_DeleteControlArea"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Control Area: AT Control Area";
        
        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + "662");
        
        ControlAreaDetailPage editPage = new ControlAreaDetailPage(driverExt, Urls.DemandResponse.CONTROL_AREA_DETAILS + "662");

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }    
    
    @Test(enabled = true, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_11_DeleteControlArea"})
    public void deleteControlAreaSuccess() {
        final String EXPECTED_MSG = "AT Delete Control Area deleted successfully.";
        
        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + "589");

        ControlAreaDetailPage detailPage = new ControlAreaDetailPage(driverExt, Urls.DemandResponse.CONTROL_AREA_DETAILS + "589");
        
        ConfirmModal  confirmModal = detailPage.showDeleteControlAreaModal();
        
        confirmModal.clickOkAndWait();
        
        waitForPageToLoad("Setup", Optional.empty());
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.DemandResponse.SETUP_FILTER + Urls.Filters.CONTROL_AREA);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }  

}
