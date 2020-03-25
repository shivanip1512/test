package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.eaton.pages.capcontrol.CbcEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CbcEditTests extends SeleniumTestSetup {

    DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit CBC: AT CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "670" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 670);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects" })
    public void editCbcRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CBC was successfully saved.";
        
        navigate(Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 563);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt, 563);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteCbcSuccess() {
        final String EXPECTED_MSG = "Deleted CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "577" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 577);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
