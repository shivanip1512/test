package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;

public class AmiDashboardTests extends SeleniumTestSetup {

    private AmiDashboardPage amiPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Ami.DASHBOARD);

        amiPage = new AmiDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Ami.DASHBOARD, TestConstants.Ami.AMI})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Dashboard: Default AMI Dashboard";
        
        String actualPageTitle = amiPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
