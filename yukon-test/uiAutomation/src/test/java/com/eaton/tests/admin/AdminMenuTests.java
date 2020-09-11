package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AdminMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ADMIN_INDEX = 5;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.HOME);
        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_ConfigurationUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 0);

        assertThat(url).contains(Urls.Admin.CONFIGURATION);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_EnergyCompanyUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 1);

        assertThat(url).contains(Urls.Admin.ENERGY_COMPANY);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_MaintenanceUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 2);

        assertThat(url).contains(Urls.Admin.MAINTENANCE);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_MultiSpeakUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 3);

        assertThat(url).contains(Urls.Admin.MULTI_SPEAK);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_SubstationsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 4);

        assertThat(url).contains(Urls.Admin.SUBSTATIONS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_UsersAndGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 5);

        assertThat(url).contains(Urls.Admin.USERS_AND_GROUPS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_ReportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 6);

        assertThat(url).contains(Urls.Admin.REPORTS);
    }
}