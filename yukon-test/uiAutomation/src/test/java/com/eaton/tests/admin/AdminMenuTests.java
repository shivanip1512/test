package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AdminMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ADMIN_INDEX = 5;
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
        
        assertThat(url).contains(Urls.Admin.CONFIGURATION);
    }
    
    @Test
    public void energyCompanyUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 1);
        
        System.out.println("2 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.ENERGY_COMPANY);
    }
    
    @Test
    public void maintenanceUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 2);
        
        System.out.println("3 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.MAINTENANCE);
    }
    
    @Test
    public void multiSpeakUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 3);
        
        System.out.println("4 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.MULTI_SPEAK);
    }
    
    @Test
    public void substationsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 4);
        
        System.out.println("5 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.SUBSTATIONS);
    }
    
    @Test
    public void usersAndGroupsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 5);
        
        System.out.println("6 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.USERS_AND_GROUPS);
    }
    
    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 6);
        
        System.out.println("7 " + Thread.currentThread().getId());

        assertThat(url).contains(Urls.Admin.REPORTS);
    }
}
