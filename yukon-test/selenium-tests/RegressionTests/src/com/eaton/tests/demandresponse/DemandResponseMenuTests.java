package com.eaton.tests.demandresponse;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class DemandResponseMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final String DEMAND_RESPONSE =  "Demand Response";
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt, getBaseUrl());
    }
    
    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void dashboardUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Dashboard");

        Assert.assertTrue(url.contains(Urls.DemandResponse.DASHBOARD), EXPECTED + Urls.DemandResponse.DASHBOARD + ACTUAL + url);
    }

    @Test
    public void scenariosUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Scenarios");

        Assert.assertTrue(url.contains(Urls.DemandResponse.SCENARIOS), EXPECTED + Urls.DemandResponse.SCENARIOS + ACTUAL + url);
    }
    
    @Test
    public void controlAreasUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Control Areas");

        Assert.assertTrue(url.contains(Urls.DemandResponse.CONTROL_AREA), EXPECTED + Urls.DemandResponse.CONTROL_AREA + ACTUAL + url);
    }
    
    @Test
    public void programsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Programs");

        Assert.assertTrue(url.contains(Urls.DemandResponse.PROGRAMS), EXPECTED + Urls.DemandResponse.PROGRAMS + ACTUAL + url);
    }
    
    @Test
    public void loadGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Load Groups");

        Assert.assertTrue(url.contains(Urls.DemandResponse.LOAD_GROUPS), EXPECTED + Urls.DemandResponse.LOAD_GROUPS + ACTUAL + url);
    }    
    
    @Test
    public void setupUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Setup");

        Assert.assertTrue(url.contains(Urls.DemandResponse.SETUP), EXPECTED + Urls.DemandResponse.SETUP + ACTUAL + url);
    }
    
    @Test
    public void ciCurtailmentUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "C&I Curtailment");

        Assert.assertTrue(url.contains(Urls.DemandResponse.CI_CURTAILMENT), EXPECTED + Urls.DemandResponse.CI_CURTAILMENT + ACTUAL + url);
    }    
    
    @Test
    public void bulkUpdateUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Bulk Update");

        Assert.assertTrue(url.contains(Urls.DemandResponse.BULK_UPDATE), EXPECTED + Urls.DemandResponse.BULK_UPDATE + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(DEMAND_RESPONSE, "Reports");

        Assert.assertTrue(url.contains(Urls.DemandResponse.REPORTS), EXPECTED + Urls.DemandResponse.REPORTS + ACTUAL + url);
    }
}
