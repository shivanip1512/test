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
import com.eaton.pages.capcontrol.SubstationDetailPage;
import com.eaton.pages.capcontrol.SubstationEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Substation: AT Substation";
        
        navigate(Urls.CapControl.SUBSTATION_EDIT + "666" + Urls.EDIT);
        
        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "666" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editSubstationRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Substation was saved successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_EDIT + "451" + Urls.EDIT);
        
        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "451/edit");        
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Substation " + timeStamp; 
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Substation: " + name, Optional.empty());
        
        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt, Urls.CapControl.SUBSTATION_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }     
    
    @Test(enabled = false, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteSubstationSuccess() {
        final String EXPECTED_MSG = "Substation AT Delete Substation deleted successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);

        SubstationEditPage editPage = new SubstationEditPage(driverExt, Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
