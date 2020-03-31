package com.eaton.tests.tools;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class ToolsMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int TOOLS_INDEX = 4;
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test
    public void collectionActionsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 0);
        
        Assert.assertTrue(url.contains(Urls.Tools.COLLECTION_ACTIONS), EXPECTED + Urls.Tools.COLLECTION_ACTIONS + ACTUAL + url);
    }

    @Test
    public void commanderUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 1);

        Assert.assertTrue(url.contains(Urls.Tools.COMMANDER), EXPECTED + Urls.Tools.COMMANDER + ACTUAL + url);
    }
    
    @Test
    public void dataExportActionsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 2);

        Assert.assertTrue(url.contains(Urls.Tools.DATA_EXPORTER), EXPECTED + Urls.Tools.DATA_EXPORTER + ACTUAL + url);
    }
    
    @Test
    public void dataViewerUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 3);

        Assert.assertTrue(url.contains(Urls.Tools.DATA_VIEWER), EXPECTED + Urls.Tools.DATA_VIEWER + ACTUAL + url);
    }
    
    @Test
    public void deviceConfigurationUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 4);

        Assert.assertTrue(url.contains(Urls.Tools.DEVICE_CONFIGURATION), EXPECTED + Urls.Tools.DEVICE_CONFIGURATION + ACTUAL + url);
    }
    
    @Test
    public void deviceGroupsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 5);

        Assert.assertTrue(url.contains(Urls.Tools.DEVICE_GROUP), EXPECTED + Urls.Tools.DEVICE_GROUP + ACTUAL + url);
    }
    
    @Test
    public void schedulesUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 6);

        Assert.assertTrue(url.contains(Urls.Tools.SCHEDULES), EXPECTED + Urls.Tools.SCHEDULES + ACTUAL + url);
    }
    
    @Test
    public void scriptsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 7);

        Assert.assertTrue(url.contains(Urls.Tools.SCRIPTS), EXPECTED + Urls.Tools.SCRIPTS + ACTUAL + url);
    }
    
    @Test
    public void trendsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 8);

        Assert.assertTrue(url.contains(Urls.Tools.TRENDS), EXPECTED + Urls.Tools.TRENDS + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(TOOLS_INDEX, 9);

        Assert.assertTrue(url.contains(Urls.Tools.REPORTS), EXPECTED + Urls.Tools.REPORTS + ACTUAL + url);
    }
}
