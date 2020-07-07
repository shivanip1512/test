package com.eaton.tests.demandresponse.cicurtailment;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ProgramListPage;

public class CiProgramListTests extends SeleniumTestSetup {

    private ProgramListPage programPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_PROGRAM_LIST);

        programPage = new ProgramListPage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Programs";

        String actualPageTitle = programPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}