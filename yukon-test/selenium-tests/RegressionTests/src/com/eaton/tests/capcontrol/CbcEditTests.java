package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.eaton.pages.capcontrol.CbcEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CbcEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();
    }

    @Test(enabled = false, groups = { "smoketest", "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {

        navigate(Urls.CapControl.CBC_EDIT + "563/" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driver, Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit CBC:"));
    }

    @Test(enabled = false, groups = { "smoketest", "SM03_03_CreateCCObjects" })
    public void createCbcRequiredFieldsOnlySuccess() {
        
        navigate(Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driver, Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name);

        CbcDetailPage detailPage = new CbcDetailPage(driver, Urls.CapControl.CBC_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, "CBC was saved successfully.");
    }
    
    @Test(enabled = false, groups = {"smoketest", "SM03_05_DeleteCCOjects"})
    public void deleteCbcSuccess() {
        
        navigate(Urls.CapControl.CBC_EDIT + "579" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driver, Urls.CapControl.CBC_EDIT + "579" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driver, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Deleted CBC");
    }
}
