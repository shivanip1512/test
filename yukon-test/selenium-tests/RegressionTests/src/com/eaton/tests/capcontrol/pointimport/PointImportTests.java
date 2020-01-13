package com.eaton.tests.capcontrol.pointimport;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.pointimport.PointImportPage;

public class PointImportTests extends SeleniumTestSetup {

    WebDriver driver;
    PointImportPage importPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.POINT_IMPORT);

        this.importPage = new PointImportPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTEst_CapControl" })
    public void titleCorrect() {

        Assert.assertEquals(this.importPage.getTitle(), "Point Import");
    }
}
