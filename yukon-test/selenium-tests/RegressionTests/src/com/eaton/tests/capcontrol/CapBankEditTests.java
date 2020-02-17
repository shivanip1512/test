package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankDetailPage;
import com.eaton.pages.capcontrol.CapBankEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CapBankEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();                
    }

    @Test(groups = {"smoketest", "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {

        navigate(Urls.CapControl.CAP_BANK_EDIT + "459" + Urls.EDIT);

        CapBankEditPage editPage = new CapBankEditPage(driver, Urls.CapControl.CAP_BANK_EDIT + "459" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit CapBank:"));
    }
    
    @Test(groups = {"smoketest", "SM03_04_EditCCObjects"})
    public void editCapBankUpdateNameOnlySuccess() {
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + "459/edit");

        CapBankEditPage editPage = new CapBankEditPage(driver, Urls.CapControl.CAP_BANK_EDIT + "459" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CapBank " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("CapBank: " + name);
        
        CapBankDetailPage detailsPage = new CapBankDetailPage(driver, Urls.CapControl.CAP_BANK_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "CapBank was saved successfully.");
    }    
    
    @Test(enabled = false, groups = {"smoketest", "SM03_05_DeleteCCOjects"})
    public void deleteCapBankSuccess() {
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + "576" + Urls.EDIT);

        CapBankEditPage editPage = new CapBankEditPage(driver, Urls.CapControl.CAP_BANK_EDIT + "576" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driver, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "CapBank AT Delete CapBank deleted successfully.");
    }
}
