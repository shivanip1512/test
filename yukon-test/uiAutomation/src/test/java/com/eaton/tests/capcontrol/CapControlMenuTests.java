package com.eaton.tests.capcontrol;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
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
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void dashboardUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 0);

        Assert.assertTrue(url.contains(Urls.CapControl.DASHBOARD), EXPECTED + Urls.CapControl.DASHBOARD + ACTUAL + url);
    }

    @Test
    public void schedulesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 1);

        Assert.assertTrue(url.contains(Urls.CapControl.SCHEDULES), EXPECTED + Urls.CapControl.SCHEDULES + ACTUAL + url);
    }
    
    @Test
    public void strategiesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 2);

        Assert.assertTrue(url.contains(Urls.CapControl.STRATEGIES), EXPECTED + Urls.CapControl.STRATEGIES + ACTUAL + url);
    }
    
    @Test
    public void recentTempMovesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 3);

        Assert.assertTrue(url.contains(Urls.CapControl.RECENT_TEMP_MOVES), EXPECTED + Urls.CapControl.RECENT_TEMP_MOVES + ACTUAL + url);
    }
    
    @Test
    public void orphansUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 4);

        Assert.assertTrue(url.contains(Urls.CapControl.ORPHANS), EXPECTED + Urls.CapControl.ORPHANS + ACTUAL + url);
    }    
    
    @Test
    public void regulatroSetupUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 5);

        Assert.assertTrue(url.contains(Urls.CapControl.REGULATOR_SETUP), EXPECTED + Urls.CapControl.REGULATOR_SETUP + ACTUAL + url);
    }
    
    @Test
    public void importUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 6);

        Assert.assertTrue(url.contains(Urls.CapControl.IMPORT), EXPECTED + Urls.CapControl.IMPORT + ACTUAL + url);
    }    
    
    @Test
    public void pointImportUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 7);

        Assert.assertTrue(url.contains(Urls.CapControl.POINT_IMPORT), EXPECTED + Urls.CapControl.POINT_IMPORT + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(CAP_CONTROL_INDEX, 8);

        Assert.assertTrue(url.contains(Urls.CapControl.REPORTS), EXPECTED + Urls.CapControl.REPORTS + ACTUAL + url);
    }
}
