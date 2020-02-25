package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.AreaDetailPage;
import com.eaton.pages.capcontrol.AreaEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class AreaEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Area: AT Area";
        
        navigate(Urls.CapControl.AREA_EDIT + "672" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "672" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editAreaNameOnlySuccess() {   
        final String EXPECTED_MSG = "Area was saved successfully.";
        
        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Area " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Area: " + name, Optional.empty());
        
        AreaDetailPage detailsPage = new AreaDetailPage(driverExt, Urls.CapControl.AREA_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }      
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteAreaSuccess() {
        final String EXPECTED_MSG = "Area AT Delete Area deleted successfully.";
        
        navigate(Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
