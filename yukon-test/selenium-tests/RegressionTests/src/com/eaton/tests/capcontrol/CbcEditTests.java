package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.eaton.pages.capcontrol.CbcEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CbcEditTests extends SeleniumTestSetup {

    DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "CBC: AT CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "879" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, Urls.CapControl.CBC_EDIT + "879" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects" })
    public void editCbcRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CBC was saved successfully.";
        
        navigate(Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name);

        CbcDetailPage detailPage = new CbcDetailPage(driverExt, Urls.CapControl.CBC_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteCbcSuccess() {
        final String EXPECTED_MSG = "Deleted CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "579" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, Urls.CapControl.CBC_EDIT + "579" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }
}
