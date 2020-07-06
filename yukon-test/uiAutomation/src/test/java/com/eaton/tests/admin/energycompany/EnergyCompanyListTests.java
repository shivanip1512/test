package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
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

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.ENERGY_COMPANY_LIST);

        page = new EnergyCompanyListPage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.Admin.ADMIN, TestConstants.Admin.ENERGY_COMPANY})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Energy Companies";
        
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }         
}
