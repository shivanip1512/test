package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupEditPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.pages.demandresponse.LoadProgramEditPage;

public class LoadProgramEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_06_EditLoadPgm()"})
    public void pageTitleCorrect() {
        final String PROGRAM_NAME = "AT Load Program";
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "665" + Urls.EDIT);
        
        LoadProgramEditPage editPage = new LoadProgramEditPage(driverExt, 665);

        String actualPageTitle = editPage.getPageTitle();

        Assert.assertTrue(actualPageTitle.contains("Edit Load Program: " + PROGRAM_NAME), "Expected Page title: 'Edit Load Program: " + PROGRAM_NAME + "' but found: " + actualPageTitle);      
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_06_EditLoadPgm()"})
    public void editLoadProgramNameOnlySuccess() {        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "599" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, 599);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Direct Program " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Program: " + name, Optional.empty());
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, 599);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg '" + name + " saved successfully.' But found: " + userMsg);
    }       
}
