package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
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
        driverExt = getDriverExt();
        navigate(Urls.Admin.ENERGY_COMPANY_LIST);
        page = new EnergyCompanyListPage(driverExt);
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Energy Companies";
        
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }         
}
