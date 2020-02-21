package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;

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
        final String LOAD_GROUP_TITLE = "Edit Load Group: AT Load Group";
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "856" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Urls.DemandResponse.LOAD_GROUP_EDIT + "856" + Urls.EDIT);

        String pageTitle = editPage.getPageTitle();
        Assert.assertTrue(pageTitle.equals(LOAD_GROUP_TITLE));       
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_02_EditLoadGrp"})
    public void editLoadGroupNameOnlySuccess() {        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "850" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Urls.DemandResponse.LOAD_GROUP_EDIT + "850" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Edited Ecobee Ldgrp " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name);
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL + "850");
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.");
    }        
}
