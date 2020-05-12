package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseDashboardPage;

public class DemandResponseDashboardTests extends SeleniumTestSetup {

    private DemandResponseDashboardPage demandPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.DemandResponse.DASHBOARD);

        demandPage = new DemandResponseDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "DR Dashboard";
        
        String actualPageTitle = demandPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test
    public void quickSearchLinkActiveControlAreasUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Control Areas");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_CONTROL_AREAS);
    }

    @Test
    public void quickSearchLinkActiveProgramsUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Programs");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_PROGRAMS);
    }

    @Test
    public void quickSearchLinkActiveLoadGroupsUrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Load Groups");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_LOAD_GROUPS);
    }

    @Test()
    public void actionsBtnDisplayedAndEnabled() {
        softly.assertThat(demandPage.getActionBtn().isEnabled()).isTrue();
        softly.assertThat(demandPage.getActionBtn().isDisplayed()).isTrue();
        
        softly.assertAll();
    }
}
