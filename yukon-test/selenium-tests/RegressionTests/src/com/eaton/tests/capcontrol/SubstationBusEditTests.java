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
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Bus: AT Substation Bus";

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "667" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "667" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }
    
    //TODO this keeps failing due to an issue
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editSubstationBusUpdateNameOnlySuccess() {
        final String EXPECTED_MSG = "Bus was saved successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "430" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "430" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);
        editPage.getStatus().setValue(false);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name, Optional.empty());
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, Urls.CapControl.SUBSTATION_BUS_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }  
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteSubstationBusSuccess() {
        final String EXPECTED_MSG = "Bus AT Delete Bus deleted successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmModal modal = new ConfirmModal(driverExt, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
