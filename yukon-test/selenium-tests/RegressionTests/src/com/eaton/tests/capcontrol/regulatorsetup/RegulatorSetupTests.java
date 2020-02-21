package com.eaton.tests.capcontrol.regulatorsetup;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.regulatorsetup.RegulatorSetupPage;

public class RegulatorSetupTests extends SeleniumTestSetup {

    private RegulatorSetupPage regulatorSetupPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_SETUP);

        regulatorSetupPage = new RegulatorSetupPage(driverExt, null);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Regulator Setup";
        
        String actualPageTitle = regulatorSetupPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}
