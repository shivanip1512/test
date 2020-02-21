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

public class LoadGroupDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_02_EditLoadGrp"})
    public void pageTitleCorrect() {
        final String GROUP_NAME = "AT Load Group";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "856");
        
        LoadGroupDetailPage editPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_EDIT + "856");

        String pageTitle = editPage.getPageTitle();
        
        Assert.assertTrue(pageTitle.contains("Load Group: " + GROUP_NAME), "Expected Page title: 'Load Group:: " + GROUP_NAME + "' but found: " + pageTitle); 
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_03_CopyLoadGrp"})
    public void copyLoadGroupSuccess() {
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");
        
        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied RFN Expresscom Ldgrp " + timeStamp;
        modal.getName().setInputValue(name);
        
        modal.clickOk();
        
        waitForPageToLoad("Load Group: " + name);
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " copied successfully.", "Expected User Msg: '" + name + " copied succssfully' but found: " + userMsg); 
    }
    
    @Test(enabled = false, groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_04_DeleteLoadGrp"})
    public void deleteLoadGroupSuccess() {
        final String EXPECTED_MSG = "AT Delete RFN Expresscom Ldgrp deleted successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "593");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL + "593");
        
        ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal();
        
        confirmModal.clickOk();
        
        waitForPageToLoad("Setup");
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.DemandResponse.SETUP_FILTER + Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }  

}
