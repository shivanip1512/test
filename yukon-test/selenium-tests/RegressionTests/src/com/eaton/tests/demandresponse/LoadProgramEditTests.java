package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupEditPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.pages.demandresponse.LoadProgramEditPage;

public class LoadProgramEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_06_EditLoadPgm()"})
    public void pageTitleCorrect() {
        final String PROGRAM_NAME = "AT Load Program";
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "857" + Urls.EDIT);
        
        LoadProgramEditPage editPage = new LoadProgramEditPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_EDIT + "857" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();

        Assert.assertTrue(actualPageTitle.contains("Edit Load Program: " + PROGRAM_NAME), "Expected Page title: 'Edit Load Program: " + PROGRAM_NAME + "' but found: " + actualPageTitle);      
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_06_EditLoadPgm()"})
    public void editLoadProgramNameOnlySuccess() {        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "599" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_EDIT + "599" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Edited Direct Program " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Program: " + name);
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "599");
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg '" + name + " saved successfully.' But found: " + userMsg);
    }       
}
