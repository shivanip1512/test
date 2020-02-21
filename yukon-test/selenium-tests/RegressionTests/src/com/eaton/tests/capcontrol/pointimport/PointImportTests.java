package com.eaton.tests.capcontrol.pointimport;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.pointimport.PointImportPage;

public class PointImportTests extends SeleniumTestSetup {

    PointImportPage importPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();        

        driver.get(getBaseUrl() + Urls.CapControl.POINT_IMPORT);

        importPage = new PointImportPage(driverExt, null);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Point Import";
        
        String actualPageTitle = importPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}
