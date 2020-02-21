package com.eaton.tests.capcontrol.reports;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.reports.ReportsPage;

public class ReportsTests extends SeleniumTestSetup {

    private ReportsPage reportPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.REPORTS);

        reportPage = new ReportsPage(driverExt, null);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Report Selection";
        
        String actualPageTitle = reportPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}
