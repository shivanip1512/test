package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.pages.demandresponse.LoadProgramEditPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEditPage;

public class LoadProgramEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void loadProgramEdit_pageTitleCorrect() {
        final String PROGRAM_NAME = "AT Load Program";
        final String EXPECTED_TITLE = "Edit Load Program: " + PROGRAM_NAME;
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "665" + Urls.EDIT);
        
        LoadProgramEditPage editPage = new LoadProgramEditPage(driverExt, 665);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void loadProgramEdit_requiredFieldsOnlySuccess() {        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + "599" + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, 599);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Direct Program " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Program: " + name, Optional.empty());
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, 599);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }       
}
