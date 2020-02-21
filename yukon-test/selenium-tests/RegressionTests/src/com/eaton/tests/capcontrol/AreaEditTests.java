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
import com.eaton.pages.capcontrol.AreaDetailPage;
import com.eaton.pages.capcontrol.AreaEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class AreaEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Area:"));       
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editAreaNameOnlySuccess() {        
        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Area " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Area: " + name);
        
        AreaDetailPage detailsPage = new AreaDetailPage(driverExt, Urls.CapControl.AREA_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Area was saved successfully.");
    }      
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteAreaSuccess() {
        
        navigate(Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driverExt, Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Area AT Delete Area deleted successfully.");
    }
}
