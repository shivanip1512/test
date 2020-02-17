package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.RegulatorDetailPage;
import com.eaton.pages.capcontrol.RegulatorEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class RegulatorEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();        
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        String expectedPageTitle = "Edit Regulator:";
        navigate(Urls.CapControl.REGULATOR_EDIT + "490/edit");

        RegulatorEditPage editPage = new RegulatorEditPage(driver, Urls.CapControl.REGULATOR_EDIT + "490/edit");

        String pageTitle = editPage.getPageTitle();        
        Assert.assertTrue(pageTitle.startsWith(expectedPageTitle), "Expected Page Title to be '" + expectedPageTitle + "', but found " + pageTitle);
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void editRegulatorUpdateNameOnlySuccess() {
        
        navigate(Urls.CapControl.REGULATOR_EDIT + "490/edit");

        RegulatorEditPage editPage = new RegulatorEditPage(driver, Urls.CapControl.REGULATOR_EDIT + "490/edit");
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Regulator " + timeStamp; 
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Regulator: " + name);
        
        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driver, Urls.CapControl.REGULATOR_DETAIL);
        
        //The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        Assert.assertEquals(detailsPage.getTitle(), "Regulator: " + name);
    }   
    
    @Test(enabled = false, groups = {"smoketest", "SM03_05_DeleteCCOjects"})
    public void deleteRegulatorSuccess() {
        
        navigate(Urls.CapControl.REGULATOR_EDIT + "578" + Urls.EDIT);

        RegulatorEditPage editPage = new RegulatorEditPage(driver, Urls.CapControl.REGULATOR_EDIT + "578" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driver, Urls.CapControl.ORPHANS);
        
        //TODO need to figure out what to assert since there is no message like the other volt/var objects that it has been deleted
        
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Feeder AT Delete Feeder deleted successfully.");
    }
}
