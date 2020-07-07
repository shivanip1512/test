package com.eaton.tests.demandresponse.cicurtailment;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.CiProgramCreatePage;

public class CiProgramCreateTests extends SeleniumTestSetup {

    private CiProgramCreatePage createPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();

        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_PROGRAM_CREATE);

        createPage = new CiProgramCreatePage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Program";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}