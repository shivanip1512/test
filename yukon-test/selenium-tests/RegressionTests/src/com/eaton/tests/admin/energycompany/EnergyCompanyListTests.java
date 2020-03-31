package com.eaton.tests.admin.energycompany;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyListPage;

public class EnergyCompanyListTests extends SeleniumTestSetup {

    private EnergyCompanyListPage page;
    private DriverExtensions driverExt;
    private static final String FOUND = "' but found: ";
    private static final String EXPECTED = "Expected Page title: '";

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.ENERGY_COMPANY_LIST);

        page = new EnergyCompanyListPage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Energy Companies";
        
        String actualPageTitle = page.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, EXPECTED + EXPECTED_TITLE + FOUND + actualPageTitle);
    }         
}
