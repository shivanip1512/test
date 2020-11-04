package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapControlDashboardPage;

public class CapControlDashboardTests extends SeleniumTestSetup {

    private CapControlDashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        setRefreshPage(false);
        
        navigate(Urls.CapControl.DASHBOARD);
        dashboardPage = new CapControlDashboardPage(driverExt);
    }
    
    @AfterMethod()
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(dashboardPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void capControlDashboard_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Volt/Var Dashboard";

        String actualPageTitle = dashboardPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
