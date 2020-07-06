package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoPage;

public class EnergyCompanyGeneralInfoTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();               
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.Admin.ADMIN, TestConstants.Admin.ENERGY_COMPANY})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "QA_Test";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "64");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(driverExt, 64);
                                 
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }         
}
