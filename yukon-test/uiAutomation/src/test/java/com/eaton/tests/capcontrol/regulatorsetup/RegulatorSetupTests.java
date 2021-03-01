package com.eaton.tests.capcontrol.regulatorsetup;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.regulatorsetup.RegulatorSetupPage;

public class RegulatorSetupTests extends SeleniumTestSetup {

    private RegulatorSetupPage regulatorSetupPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.CapControl.REGULATOR_SETUP);
        regulatorSetupPage = new RegulatorSetupPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VOLT_VAR })
    public void regulatorSetup_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Regulator Setup";

        String actualPageTitle = regulatorSetupPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
