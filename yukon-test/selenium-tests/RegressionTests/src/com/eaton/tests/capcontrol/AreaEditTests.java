package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.AreaDetailPage;
import com.eaton.pages.capcontrol.AreaEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class AreaEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();                
    }

    @Test(groups = {"smoketest", "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driver, Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Area:"));       
    }
    
    @Test(groups = {"smoketest", "SM03_04_EditCCObjects"})
    public void editAreaNameOnlySuccess() {        
        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        AreaEditPage editPage = new AreaEditPage(driver, Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Area " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Area: " + name);
        
        AreaDetailPage detailsPage = new AreaDetailPage(driver, Urls.CapControl.AREA_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Area was saved successfully.");
    }      
    
    @Test(enabled = false, groups = {"smoketest", "SM03_05_DeleteCCOjects"})
    public void deleteAreaSuccess() {
        
        navigate(Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driver, Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driver, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Area AT Delete Area deleted successfully.");
    }
}
