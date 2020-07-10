package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        createPage = new LoadGroupCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreate_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreate_RequiredFieldsValidation() {
        createPage.getSaveBtn().click();

        softly.assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
        softly.assertThat(createPage.getType()).isEqualTo("Type is required.");
        softly.assertAll();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
