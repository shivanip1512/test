package com.eaton.tests.capcontrol.regulatorsetup;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.regulatorsetup.RegulatorSetupPage;

public class RegulatorSetupTests extends SeleniumTestSetup {

    private RegulatorSetupPage regulatorSetupPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_SETUP);

        regulatorSetupPage = new RegulatorSetupPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void regulatorSetup_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Regulator Setup";

        String actualPageTitle = regulatorSetupPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
