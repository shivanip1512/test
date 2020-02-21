package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramCreateTests extends SeleniumTestSetup {
    
    private LoadProgramCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        createPage = new LoadProgramCreatePage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_CREATE);                
    }  
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_05_CreateLoadPgm()"})
    public void pageTitleCorrect() {
        Assert.assertEquals(createPage.getPageTitle(), "Create Load Program");
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_05_CreateLoadPgm()"})
    public void createLoadProgramSuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
                 
        String name = "AT LM Direct Program " + timeStamp;         
        createPage.getName().setInputValue(name);        
        createPage.getType().selectItemByText("LM Direct Program");
        waitForLoadingSpinner();
        
        CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal();
        
        modal.getGearName().setInputValue("TC " + timeStamp); 
        modal.getGearType().selectItemByText("True Cycle");   
        waitForLoadingSpinner();
        modal.clickOk();
        
        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();
        
        groupsTab.clickTab("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable("AT RFN Expresscom Ldgrp for Create Ldprgm");
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Program: " + name);
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_DETAILS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.");
    } 
    
    //DELETE ME deleted successfully.
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}
