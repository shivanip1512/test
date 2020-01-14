package com.eaton.tests.capcontrol.orphans;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.SubstationBusCreatePage;

public class SubstationBusCreateTests extends SeleniumTestSetup {

    private SubstationBusCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_BUS_CREATE);

        this.createPage = new SubstationBusCreatePage(driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Bus");
    }
}
