package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ProgramListPage;

public class ProgramListTests extends SeleniumTestSetup {

    private ProgramListPage listPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.DemandResponse.PROGRAMS);

        listPage = new ProgramListPage(driverExt);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Programs";
        
        String actualPageTitle = listPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test
    public void columnHeadersCorrect() {
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
