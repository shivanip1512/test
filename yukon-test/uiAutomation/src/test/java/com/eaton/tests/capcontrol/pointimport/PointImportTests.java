package com.eaton.tests.capcontrol.pointimport;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.pointimport.PointImportPage;

public class PointImportTests extends SeleniumTestSetup {

    private PointImportPage importPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.CapControl.POINT_IMPORT);
        importPage = new PointImportPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VOLT_VAR })
    public void pointImport_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Point Import";

        String actualPageTitle = importPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
