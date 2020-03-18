package com.eaton.tests.admin.energycompany;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoPage;

public class EnergyCompanyGeneralInfoTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();               
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, ""})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "QA_Test";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "64");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(driverExt, Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "64");
                                 
        String actualPageTitle = page.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }         
}
