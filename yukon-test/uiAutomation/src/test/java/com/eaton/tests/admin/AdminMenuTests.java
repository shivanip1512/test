package com.eaton.tests.admin;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AdminMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ADMIN_INDEX = 5;
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";
    private String baseUrl;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        
        baseUrl = getBaseUrl();

        driver.get(baseUrl + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test
    public void configurationUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 0);

        System.out.println("1 " + Thread.currentThread().getId());
        
        Assert.assertTrue(url.contains(Urls.Admin.CONFIGURATION), EXPECTED + baseUrl + Urls.Admin.CONFIGURATION + ACTUAL + url);        
    }
    
    @Test
    public void energyCompanyUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 1);
        
        System.out.println("2 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.ENERGY_COMPANY), EXPECTED + baseUrl + Urls.Admin.ENERGY_COMPANY + ACTUAL + url);
    }
    
    @Test
    public void maintenanceUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 2);
        
        System.out.println("3 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.MAINTENANCE), EXPECTED + baseUrl + Urls.Admin.MAINTENANCE + ACTUAL + url);
    }
    
    @Test
    public void multiSpeakUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 3);
        
        System.out.println("4 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.MULTI_SPEAK), EXPECTED + baseUrl + Urls.Admin.MULTI_SPEAK + ACTUAL + url);
    }
    
    @Test
    public void substationsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 4);
        
        System.out.println("5 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.SUBSTATIONS), EXPECTED + baseUrl + Urls.Admin.SUBSTATIONS + ACTUAL + url);
    }
    
    @Test
    public void usersAndGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 5);
        
        System.out.println("6 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.USERS_AND_GROUPS), EXPECTED + baseUrl + Urls.Admin.USERS_AND_GROUPS + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 6);
        
        System.out.println("7 " + Thread.currentThread().getId());

        Assert.assertTrue(url.contains(Urls.Admin.REPORTS), EXPECTED + baseUrl + Urls.Admin.REPORTS + ACTUAL + url);
    }
}
