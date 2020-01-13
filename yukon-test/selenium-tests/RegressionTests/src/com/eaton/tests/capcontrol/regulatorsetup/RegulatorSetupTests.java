package com.eaton.tests.capcontrol.regulatorsetup;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.regulatorsetup.RegulatorSetupPage;

public class RegulatorSetupTests extends SeleniumTestSetup {

    WebDriver driver;
    RegulatorSetupPage regulatorSetupPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_SETUP);

        this.regulatorSetupPage = new RegulatorSetupPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void titleCorrect() {

        Assert.assertEquals(this.regulatorSetupPage.getTitle(), "Regulator Setup");
    }
}
