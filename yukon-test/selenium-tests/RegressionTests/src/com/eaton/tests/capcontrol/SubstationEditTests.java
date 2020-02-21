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
import com.eaton.pages.capcontrol.SubstationDetailPage;
import com.eaton.pages.capcontrol.SubstationEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        navigate(Urls.CapControl.SUBSTATION_EDIT + "451" + Urls.EDIT);
        
        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "451/edit");

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Substation:")); 
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editSubstationRequiredFieldsOnlySuccess() {
        navigate(Urls.CapControl.SUBSTATION_EDIT + "451" + Urls.EDIT);
        
        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "451/edit");        
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Substation " + timeStamp; 
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Substation: " + name);
        
        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt, Urls.CapControl.SUBSTATION_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Substation was saved successfully.");
    }     
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteSubstationSuccess() {
        
        navigate(Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);

        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Substation AT Delete Substation deleted successfully.");
    }
}
