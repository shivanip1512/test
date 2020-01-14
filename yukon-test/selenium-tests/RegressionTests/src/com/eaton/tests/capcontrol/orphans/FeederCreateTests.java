package com.eaton.tests.capcontrol.orphans;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.FeederCreatePage;

public class FeederCreateTests extends SeleniumTestSetup {

    private FeederCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.FEEDER_CREATE);

        createPage = new FeederCreatePage(driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Feeder");
    }
}
