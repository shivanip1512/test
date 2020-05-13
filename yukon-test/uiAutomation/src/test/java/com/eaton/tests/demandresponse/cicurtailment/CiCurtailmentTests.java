package com.eaton.tests.demandresponse.cicurtailment;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.CiCurtailmentPage;

public class CiCurtailmentTests extends SeleniumTestSetup {

    private CiCurtailmentPage curtailmentPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_CURTAILMENT);

        curtailmentPage = new CiCurtailmentPage(driverExt, null);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Commercial Curtailment";

        String actualPageTitle = curtailmentPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
