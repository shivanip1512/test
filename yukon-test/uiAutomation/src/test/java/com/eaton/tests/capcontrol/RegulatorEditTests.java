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
import com.eaton.pages.capcontrol.RegulatorDetailPage;
import com.eaton.pages.capcontrol.RegulatorEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class RegulatorEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();        
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Regulator: AT Regulator";
        
        navigate(Urls.CapControl.REGULATOR_EDIT + "671" + Urls.EDIT);

        RegulatorEditPage editPage = new RegulatorEditPage(driverExt, 671);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editRegulatorUpdateNameOnlySuccess() {
        
        navigate(Urls.CapControl.REGULATOR_EDIT + "490" + Urls.EDIT);

        RegulatorEditPage editPage = new RegulatorEditPage(driverExt, 490);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Regulator " + timeStamp; 
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Regulator: " + name, Optional.empty());
        
        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driverExt, 490);
        
        //The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        String actualPageTitle = detailsPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, "Regulator: " + name, "Expected Page title: 'Regulator: " + name + "' but found: " + actualPageTitle);
    }   
    
    @Test(enabled = false, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteRegulatorSuccess() {
        
        navigate(Urls.CapControl.REGULATOR_EDIT + "578" + Urls.EDIT);

        RegulatorEditPage editPage = new RegulatorEditPage(driverExt, 578);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        //TODO need to figure out what to assert since there is no message like the other volt/var objects that it has been deleted
        
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Feeder AT Delete Feeder deleted successfully.");
    }
}
