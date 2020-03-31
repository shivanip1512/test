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
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Bus: AT Substation Bus";

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "667" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 667);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editSubstationBusUpdateNameOnlySuccess() {
        final String EXPECTED_MSG = "Bus was saved successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "430" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 430);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name, Optional.empty());
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, 430);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }  
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteSubstationBusSuccess() {
        final String EXPECTED_MSG = "Bus AT Delete Bus deleted successfully.";
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 574);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
