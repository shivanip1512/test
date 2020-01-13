package com.eaton.tests.capcontrol.orphans;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.CbcCreatePage;

public class CbcCreateTests extends SeleniumTestSetup {

    WebDriver driver;
    CbcCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.CBC_CREATE);

        this.createPage = new CbcCreatePage(this.driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create CBC");
    }
}
