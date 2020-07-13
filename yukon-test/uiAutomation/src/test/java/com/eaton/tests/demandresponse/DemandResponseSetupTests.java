package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;

public class DemandResponseSetupTests extends SeleniumTestSetup {

    private DemandResponseSetupPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.SETUP);

        page = new DemandResponseSetupPage(driverExt);
        
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseSetup_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Setup";
        
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}