package com.eaton.tests.assets;

import static org.assertj.core.api.Assertions.assertThat;

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
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.HOME);
        page = new HomePage(driverExt);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Dashboard_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 0);

        assertThat(url).contains(Urls.Assets.DASHBOARD);
    }
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Gateways_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 1);

        assertThat(url).contains(Urls.Assets.GATEWAYS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Relays_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 2);

        assertThat(url).contains(Urls.Assets.RELAYS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Rtus_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 3);

        assertThat(url).contains(Urls.Assets.RTUS);
    }
        
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_VirtualDevices_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 4);

        assertThat(url).contains(Urls.Assets.VIRTUAL_DEVICES);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_OptOutStatus_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 5);

        assertThat(url).contains(Urls.Assets.OPT_OUT_STATUS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_WorkOrders_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 6);

        assertThat(url).contains(Urls.Assets.WORK_ORDERS);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Import_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 7);

        assertThat(url).contains(Urls.Assets.IMPORT);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsMenu_Reports_UrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 8);

        assertThat(url).contains(Urls.Assets.REPORTS);
    }
}
