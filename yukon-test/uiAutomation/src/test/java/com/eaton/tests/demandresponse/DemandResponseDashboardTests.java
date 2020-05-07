package com.eaton.tests.demandresponse;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseDashboardPage;

public class DemandResponseDashboardTests extends SeleniumTestSetup {

    private DemandResponseDashboardPage demandPage;
    private SoftAssert softAssertion;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.DASHBOARD);

        demandPage = new DemandResponseDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "DR Dashboard";
        
        String actualPageTitle = demandPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void quickSearchLinkActiveControlAreasUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Control Areas");

        Assert.assertTrue(url.contains(Urls.DemandResponse.ACTIVE_CONTROL_AREAS));
    }

    @Test
    public void quickSearchLinkActiveProgramsUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Programs");

        Assert.assertTrue(url.contains(Urls.DemandResponse.ACTIVE_PROGRAMS));
    }

    @Test
    public void quickSearchLinkActiveLoadGroupsUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Load Groups");

        Assert.assertTrue(url.contains(Urls.DemandResponse.ACTIVE_LOAD_GROUPS));
    }

    @Test()
    public void actionsBtnDisplayedAndEnabled() {
        softAssertion.assertTrue(demandPage.getActionBtn().isEnabled());
        softAssertion.assertTrue(demandPage.getActionBtn().isDisplayed());
    }
}
