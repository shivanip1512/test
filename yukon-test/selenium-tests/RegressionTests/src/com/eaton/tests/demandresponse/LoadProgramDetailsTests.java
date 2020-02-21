package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_07_CopyLoadPgm()()"})
    public void pageTitleCorrect() {
        
        final String PROGRAM_NAME = "AT Load Program";
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "857");
        
        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "857");

        String pageTitle = detailPage.getPageTitle();
        
        Assert.assertTrue(pageTitle.contains("Load Program: " + PROGRAM_NAME), "Expected Page title: 'Load Program: " + PROGRAM_NAME + "' but found: " + pageTitle);        
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_07_CopyLoadPgm()"})
    public void copyLoadProgramSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "604");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "604");
        
        CopyLoadGroupModal modal = detailPage.showCopyLoadProgramModal();
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied Program " + timeStamp;
        modal.getName().setInputValue(name);
        
        modal.clickOk();
        
        waitForPageToLoad("Load Program: " + name);
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, Urls.DemandResponse.LOAD_PROGRAM_DETAILS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name +" copied successfully.");
    }
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_08_DeleteLoadPgm()"})
    public void deleteLoadProgramSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "605");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL + "605");
        
        ConfirmModal modal = detailPage.showDeleteLoadGroupModal();
        
        modal.clickOk();
        
        waitForPageToLoad("Setup");
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.DemandResponse.SETUP_FILTER + Urls.Filters.LOAD_PROGRAM);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "AT Delete Direct Program deleted successfully.");
    }   

}
