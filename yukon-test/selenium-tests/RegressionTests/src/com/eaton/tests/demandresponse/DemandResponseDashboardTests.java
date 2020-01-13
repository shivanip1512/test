package com.eaton.tests.demandresponse;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseDashboardPage;

public class DemandResponseDashboardTests extends SeleniumTestSetup {

    WebDriver driver;
    DemandResponseDashboardPage demandPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.DASHBOARD);

        // this.demandPage = getInstance(DemandResponseDashboardPage.class);

        this.demandPage = new DemandResponseDashboardPage(this.driver, getBaseUrl());
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.demandPage.getTitle(), "DR Dashboard");
    }

    @Test
    public void quickSearchLinkActiveControlAreasUrlCorrect() {
        String url = this.demandPage.getQuickSearchesUrl("Active Control Areas");

        Assert.assertTrue(url.contains("/dr/controlArea/list?state=active"));
    }

    @Test
    public void quickSearchLinkActiveProgramsUrlCorrect() {
        String url = this.demandPage.getQuickSearchesUrl("Active Programs");

        Assert.assertTrue(url.contains("/dr/program/list?state=ACTIVE"));
    }

    @Test
    public void quickSearchLinkActiveLoadGroupsUrlCorrect() {
        String url = this.demandPage.getQuickSearchesUrl("Active Load Groups");

        Assert.assertTrue(url.contains("/dr/loadGroup/list?state=active"));
    }

    @Test(enabled = false)
    public void resetSeasonControlHoursModelTitleCorrect() {
        // TODO get modals update to be able to grab them uniquely
    }
}
