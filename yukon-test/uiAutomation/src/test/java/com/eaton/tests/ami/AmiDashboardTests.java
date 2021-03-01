package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;

public class AmiDashboardTests extends SeleniumTestSetup {

    private AmiDashboardPage amiPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.Ami.DASHBOARD);
        amiPage = new AmiDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.AMI })
    public void amiDashboard_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Dashboard: Default AMI Dashboard";
        
        String actualPageTitle = amiPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
