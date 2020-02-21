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
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Bus:"));
    }
    
    //TODO this keeps failing due to an issue
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editSubstationBusUpdateNameOnlySuccess() {
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);
        editPage.getStatus().setValue(false);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name);
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, Urls.CapControl.SUBSTATION_BUS_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Bus was saved successfully.");
    }  
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteSubstationBusSuccess() {
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Bus AT Delete Bus deleted successfully.");
    }
}
