package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_07_CopyLoadPgm()()"})
    public void pageTitleCorrect() {        
        final String EXPECTED_TITLE = "Load Program: AT Load Program";
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "665");
        
        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 665);

        String actualPageTitle = detailPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_07_CopyLoadPgm()"})
    public void copyLoadProgramSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "604");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 604);
        
        CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied Program " + timeStamp;
        modal.getName().setInputValue(name);
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Load Program: " + name, Optional.of(8));
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, 604);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name +" copied successfully.", "Expected User Msg: '" + name +" copied successfully.' but found: " + userMsg);
    }
    
    @Test(enabled = true, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_08_DeleteLoadPgm()"})
    public void deleteLoadProgramSuccess() {
        final String EXPECTED_MSG = "AT Delete Direct Program deleted successfully.";
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "605");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 605);
        
        ConfirmModal modal = detailPage.showDeleteLoadProgramModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Setup", Optional.empty());
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_PROGRAM);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }   
}
