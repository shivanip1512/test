package com.eaton.tests.demandresponse;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ScenariosListPage;

public class ScenariosListTests extends SeleniumTestSetup {

    DriverExtensions driverExt;
    ScenariosListPage listPage;
    SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.DemandResponse.SCENARIOS);
        this.listPage = new ScenariosListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Scenarios";

        String actualPageTitle = listPage.getPageTitle();

        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void scenarioList_ColumnHeaders_Correct() {
        softly = new SoftAssertions();
        final int EXPECTED_COUNT = 2;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertAll();
    }
}
