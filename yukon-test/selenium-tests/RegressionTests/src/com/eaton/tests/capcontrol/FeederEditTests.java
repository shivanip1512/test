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
import com.eaton.pages.capcontrol.FeederDetailPage;
import com.eaton.pages.capcontrol.FeederEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class FeederEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {
        
        navigate(Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Feeder:"));
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void editFeederRequiredFieldsOnlySuccess() {
        
        navigate(Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Feeder " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Feeder: " + name);

        FeederDetailPage detailsPage = new FeederDetailPage(driverExt, Urls.CapControl.FEEDER_DETAIL);

        String userMsg = detailsPage.getUserMessage();

        Assert.assertEquals(userMsg, "Feeder was saved successfully.");
    }
    
    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteFeederSuccess() {
        
        navigate(Urls.CapControl.FEEDER_EDIT + "575" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, Urls.CapControl.FEEDER_EDIT + "575" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Feeder AT Delete Feeder deleted successfully.");
    }
}
