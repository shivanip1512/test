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
import com.eaton.pages.capcontrol.FeederDetailPage;
import com.eaton.pages.capcontrol.FeederEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class FeederEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Feeder: AT Feader";
        
        navigate(Urls.CapControl.FEEDER_EDIT + "668" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, 668);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + FOUND + actualPageTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void editFeederRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Feeder was saved successfully.";
        
        navigate(Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, 458);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Feeder " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Feeder: " + name, Optional.empty());

        FeederDetailPage detailsPage = new FeederDetailPage(driverExt, 458);

        String userMsg = detailsPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND+ userMsg);
    }
    
    @Test(enabled = true, groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteFeederSuccess() {
        final String EXPECTED_MSG = "Feeder AT Delete Feeder deleted successfully.";
                
        navigate(Urls.CapControl.FEEDER_EDIT + "575" + Urls.EDIT);

        FeederEditPage editPage = new FeederEditPage(driverExt, 575);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + FOUND + userMsg);
    }
}
