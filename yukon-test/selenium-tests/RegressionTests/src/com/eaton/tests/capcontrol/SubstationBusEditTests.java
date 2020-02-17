package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();        
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driver, Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Bus:"));
    }
    
    //TODO this keeps failing due to an issue
    @Test(enabled = false, groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void editSubstationBusUpdateNameOnlySuccess() {
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driver, Urls.CapControl.SUBSTATION_BUS_EDIT + "699" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name);
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driver, Urls.CapControl.SUBSTATION_BUS_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Bus was saved successfully.");
    }  
    
    @Test(enabled = false, groups = {"smoketest", "SM03_05_DeleteCCOjects"})
    public void deleteSubstationBusSuccess() {
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driver, Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);
        
        editPage.getDeleteBtn().click();   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Orphans");
        
        OrphansPage detailsPage = new OrphansPage(driver, Urls.CapControl.ORPHANS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Bus AT Delete Bus deleted successfully.");
    }
}
