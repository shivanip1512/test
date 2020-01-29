package com.eaton.tests.capcontrol;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.RegulatorCreatePage;

public class RegulatorCreateTests extends SeleniumTestSetup {

    private RegulatorCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_CREATE);

        createPage = new RegulatorCreatePage(driver, null);
    }

    @Test
    public void pageTitleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create Regulator");
    }
}
