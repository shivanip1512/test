package com.eaton.tests.capcontrol;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcCreatePage;

public class CbcCreateTests extends SeleniumTestSetup {

    private CbcCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.CBC_CREATE);

        this.createPage = new CbcCreatePage(driver, null);
    }

    @Test
    public void pageTitleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create CBC");
    }
}
