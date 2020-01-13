package com.eaton.tests.capcontrol.orphans;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.RegulatorCreatePage;

public class RegulatorCreateTests extends SeleniumTestSetup {

    WebDriver driver;
    RegulatorCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_CREATE);

        this.createPage = new RegulatorCreatePage(this.driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create Regulator");
    }
}
