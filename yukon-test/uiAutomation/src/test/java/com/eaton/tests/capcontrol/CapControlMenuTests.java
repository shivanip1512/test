package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class CapControlMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int CAP_CONTROL_INDEX = 2;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void dashboardUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 0);

        assertThat(url).contains(Urls.CapControl.DASHBOARD);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void schedulesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 1);

        assertThat(url).contains(Urls.CapControl.SCHEDULES);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void strategiesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 2);

        assertThat(url).contains(Urls.CapControl.STRATEGIES);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void recentTempMovesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 3);

        assertThat(url).contains(Urls.CapControl.RECENT_TEMP_MOVES);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void orphansUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 4);

        assertThat(url).contains(Urls.CapControl.ORPHANS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void regulatroSetupUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 5);

        assertThat(url).contains(Urls.CapControl.REGULATOR_SETUP);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void importUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 6);

        assertThat(url).contains(Urls.CapControl.IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void pointImportUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 7);

        assertThat(url).contains(Urls.CapControl.POINT_IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.VoltVar.VOLT_VAR })
    public void reportsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 8);

        assertThat(url).contains(Urls.CapControl.REPORTS);
    }
}
