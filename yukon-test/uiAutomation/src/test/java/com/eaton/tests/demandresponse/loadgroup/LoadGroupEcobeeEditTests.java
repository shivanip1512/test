package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupEcobeeEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: AT Load Group";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "664" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, 664);

        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void editLoadGroupNameOnlySuccess() {              
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + "596" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, 596);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Ecobee Ldgrp " + timeStamp;
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, 596);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }        
}
