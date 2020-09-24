package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
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

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.DemandResponse.DASHBOARD);
        demandPage = new DemandResponseDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void demandResponseDashboard_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "DR Dashboard";

        String actualPageTitle = demandPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void demandResponseDashboard_quickSearchLinkActiveControlAreas_UrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Control Areas");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_CONTROL_AREAS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void demandResponseDashboard_quickSearchLinkActivePrograms_UrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Programs");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_PROGRAMS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void demandResponseDashboard_quickSearchLinkActiveLoadGroups_UrlCorrect() {
        String url = demandPage.getQuickSearchesUrl("Active Load Groups");

        assertThat(url).contains(Urls.DemandResponse.ACTIVE_LOAD_GROUPS);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void demandResponseDashboard_ActionsBtn_DisplayedAndEnabled() {
        softly = new SoftAssertions();
        
        softly.assertThat(demandPage.getActionBtn().isEnabled()).isTrue();
        softly.assertThat(demandPage.getActionBtn().isDisplayed()).isTrue();
        softly.assertAll();
    }
}
