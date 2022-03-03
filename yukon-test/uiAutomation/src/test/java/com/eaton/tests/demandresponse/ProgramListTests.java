package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ProgramListPage;

public class ProgramListTests extends SeleniumTestSetup {

    private ProgramListPage listPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.DemandResponse.PROGRAMS);
        listPage = new ProgramListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void programList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Programs";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void programList_ColumnHeaders_Correct() {
        softly = new SoftAssertions();
        final int EXPECTED_COUNT = 8;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("State");
        softly.assertThat(headers).contains("Start");
        softly.assertThat(headers).contains("Stop");
        softly.assertThat(headers).contains("Current Gear");
        softly.assertThat(headers).contains("Priority");
        softly.assertThat(headers).contains("Reduction");
        softly.assertAll();
    }
}
