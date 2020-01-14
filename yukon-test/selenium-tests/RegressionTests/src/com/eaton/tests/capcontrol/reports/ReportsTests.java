package com.eaton.tests.capcontrol.reports;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.reports.ReportsPage;

public class ReportsTests extends SeleniumTestSetup {

    private ReportsPage reportPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.REPORTS);

        reportPage = new ReportsPage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void titleCorrect() {

        Assert.assertEquals(reportPage.getTitle(), "Report Selection");
    }
}
