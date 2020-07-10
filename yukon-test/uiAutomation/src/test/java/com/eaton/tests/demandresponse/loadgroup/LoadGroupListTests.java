package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupListPage;

public class LoadGroupListTests extends SeleniumTestSetup {

    private LoadGroupListPage listPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUPS);

        listPage = new LoadGroupListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Load Groups";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 6;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);

        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("State");
        softly.assertThat(headers).contains("Last Action");
        softly.assertThat(headers).contains("Day/Month/Season/Year Hrs");
        softly.assertThat(headers).contains("Reduction");

        softly.assertAll();
    }
}
