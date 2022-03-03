package com.eaton.tests.demandresponse.cicurtailment;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.CiCurtailmentPage;

public class CiCurtailmentTests extends SeleniumTestSetup {

    private CiCurtailmentPage curtailmentPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.DemandResponse.CI_CURTAILMENT);
        curtailmentPage = new CiCurtailmentPage(driverExt, null);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ciCurtailment_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Commercial Curtailment";

        String actualPageTitle = curtailmentPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
