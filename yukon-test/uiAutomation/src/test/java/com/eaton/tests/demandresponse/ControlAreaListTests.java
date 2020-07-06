package com.eaton.tests.demandresponse;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaListPage;

@Test(groups = TestConstants.DemandResponse.DEMAND_RESPONSE)
public class ControlAreaListTests extends SeleniumTestSetup {

    private ControlAreaListPage listPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.DemandResponse.CONTROL_AREA);

        listPage = new ControlAreaListPage(driverExt);
    }
    
    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Control Areas";
        
        String actualPageTitle = listPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 8;
        
        List<String> headers = this.listPage.getTable().getListTableHeaders();
        
        softly.assertThat(headers.size()).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("State");
        softly.assertThat(headers).contains("Value / Threshold");
        softly.assertThat(headers).contains("Peak / Projection");
        softly.assertThat(headers).contains("ATKU");
        softly.assertThat(headers).contains("Priority");
        softly.assertThat(headers).contains("Time Window");
        
        softly.assertAll();
    }
}
