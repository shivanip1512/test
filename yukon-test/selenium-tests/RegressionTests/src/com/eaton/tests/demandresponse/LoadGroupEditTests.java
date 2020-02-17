package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupEditTests extends SeleniumTestSetup {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        driver = getDriver();                
    }

    @Test(groups = {"smoketest", "SM06_02_EditLoadGrp"})
    public void pageTitleCorrect() {
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driver, Urls.CapControl.AREA_EDIT + "596" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.startsWith("Edit Load Group:"));       
    }
    
    @Test(groups = {"smoketest", "SM06_02_EditLoadGrp"})
    public void editLoadGroupNameOnlySuccess() {        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driver, Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Edited Ecobee Ldgrp " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name);
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driver, Urls.DemandResponse.LOAD_GROUP_DETAIL + "596");
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.");
    }  
    
    @Test(groups = {"smoketest", "SM06_03_CopyLoadGrp"})
    public void copyLoadGroupSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driver, Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");
        
        detailPage.getActionBtn().clickAndSelectOptionByText("Copy");   
        
        CopyLoadGroupModal modal = new CopyLoadGroupModal(this.driver, "copy-loadGroup-popup");
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Copied AT RFN Expresscom Ldgrp " + timeStamp;
        modal.getName().setInputValue(name);
        
        modal.clickOk();
        
        waitForPageToLoad("Load Group:");
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driver, Urls.DemandResponse.LOAD_GROUP_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name +" copied successfully.");
    }
    
    @Test(enabled = false, groups = {"smoketest", "SM06_04_DeleteLoadGrp"})
    public void deleteLoadGroupSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "593");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driver, Urls.DemandResponse.LOAD_GROUP_DETAIL + "593");
        
        detailPage.getActionBtn().clickAndSelectOptionByText("Delete");   
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForPageToLoad("Setup");
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(this.driver, Urls.DemandResponse.SETUP_FILTER + Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "AT Delete Load Group deleted successfully.");
    }    
}
