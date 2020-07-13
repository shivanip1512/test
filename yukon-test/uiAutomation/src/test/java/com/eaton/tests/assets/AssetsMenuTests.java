package com.eaton.tests.assets;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AssetsMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ASSETS_INDEX = 3;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_dashboardUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 0);

        assertThat(url).contains(Urls.Assets.DASHBOARD);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_gatewaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 1);

        assertThat(url).contains(Urls.Assets.GATEWAYS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_relaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 2);

        assertThat(url).contains(Urls.Assets.RELAYS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_rtusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 3);

        assertThat(url).contains(Urls.Assets.RTUS);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_virtualDevicesUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 4);

        assertThat(url).contains(Urls.Assets.OPT_OUT_STATUS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_optOutStatusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 5);

        assertThat(url).contains(Urls.Assets.OPT_OUT_STATUS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_workOrdersUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 6);

        assertThat(url).contains(Urls.Assets.WORK_ORDERS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_importUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 7);

        assertThat(url).contains(Urls.Assets.IMPORT);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.ASSETS })
    public void assetsMenu_reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 8);

        assertThat(url).contains(Urls.Assets.REPORTS);
    }
}
