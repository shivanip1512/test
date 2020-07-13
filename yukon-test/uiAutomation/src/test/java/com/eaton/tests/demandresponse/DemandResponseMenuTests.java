package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class DemandResponseMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int DEMAND_RESPONSE_INDEX = 1;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_dashboardUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 0);

        assertThat(url).contains(Urls.DemandResponse.DASHBOARD);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_scenariosUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 1);

        assertThat(url).contains(Urls.DemandResponse.SCENARIOS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_controlAreasUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 2);

        assertThat(url).contains(Urls.DemandResponse.CONTROL_AREA);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_programsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 3);

        assertThat(url).contains(Urls.DemandResponse.PROGRAMS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_loadGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 4);

        assertThat(url).contains(Urls.DemandResponse.LOAD_GROUPS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_setupUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 5);

        assertThat(url).contains(Urls.DemandResponse.SETUP);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_ciCurtailmentUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 6);

        assertThat(url).contains(Urls.DemandResponse.CI_CURTAILMENT);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_bulkUpdateUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 7);

        assertThat(url).contains(Urls.DemandResponse.BULK_UPDATE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void demandResponseMenu_reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE_INDEX, 8);

        assertThat(url).contains(Urls.DemandResponse.REPORTS);
    }
}
