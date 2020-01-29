package com.eaton.tests.admin;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AdminMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final String ADMIN = "Admin";
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driver, getBaseUrl());
    }

    @Test
    public void configurationUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Configuration");

        Assert.assertTrue(url.contains(Urls.Admin.CONFIGURATION), EXPECTED + Urls.Admin.CONFIGURATION + ACTUAL + url);
    }
    
    @Test
    public void energyCompanyUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Energy Company");

        Assert.assertTrue(url.contains(Urls.Admin.ENERGY_COMPANY), EXPECTED + Urls.Admin.ENERGY_COMPANY + ACTUAL + url);
    }
    
    @Test
    public void maintenanceUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Maintenance");

        Assert.assertTrue(url.contains(Urls.Admin.MAINTENANCE), EXPECTED + Urls.Admin.MAINTENANCE + ACTUAL + url);
    }
    
    @Test
    public void multiSpeakUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "MultiSpeak");

        Assert.assertTrue(url.contains(Urls.Admin.MULTI_SPEAK), EXPECTED + Urls.Admin.MULTI_SPEAK + ACTUAL + url);
    }
    
    @Test
    public void substationsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Substations");

        Assert.assertTrue(url.contains(Urls.Admin.SUBSTATIONS), EXPECTED + Urls.Admin.SUBSTATIONS + ACTUAL + url);
    }
    
    @Test
    public void usersAndGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Users and Groups");

        Assert.assertTrue(url.contains(Urls.Admin.USERS_AND_GROUPS), EXPECTED + Urls.Admin.USERS_AND_GROUPS + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN, "Reports");

        Assert.assertTrue(url.contains(Urls.Admin.REPORTS), EXPECTED + Urls.Admin.REPORTS + ACTUAL + url);
    }
}
