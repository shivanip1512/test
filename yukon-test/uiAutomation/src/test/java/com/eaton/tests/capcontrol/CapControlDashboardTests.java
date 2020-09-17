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

        navigate(Urls.CapControl.DASHBOARD);
        dashboardPage = new CapControlDashboardPage(driverExt);
    }
    
    @AfterMethod()
    public void afterMethod() {
        refreshPage(dashboardPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void capControlDashboard_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Volt/Var Dashboard";

        String actualPageTitle = dashboardPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
