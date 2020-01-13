package com.eaton.tests.capcontrol.orphans;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.FeederCreatePage;

public class FeederCreateTests extends SeleniumTestSetup {

    WebDriver driver;
    FeederCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.FEEDER_CREATE);

        this.createPage = new FeederCreatePage(this.driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create Feeder");
    }
}
