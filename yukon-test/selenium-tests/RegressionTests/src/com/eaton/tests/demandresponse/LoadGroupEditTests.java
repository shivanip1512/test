package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_02_EditLoadGrp"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: AT Load Group";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "664" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Urls.DemandResponse.LOAD_GROUP_EDIT + "664" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_02_EditLoadGrp"})
    public void editLoadGroupNameOnlySuccess() {              
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Edited Ecobee Ldgrp " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL + "596");
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg: '" + name + " saved successfully.' but found: " + userMsg);
    }        
}
