package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusCreatePage;
import com.eaton.pages.capcontrol.SubstationBusDetailPage;

public class SubstationBusCreateTests extends SeleniumTestSetup {

    private SubstationBusCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_BUS_CREATE);

        createPage = new SubstationBusCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Bus";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void createSubstationBusRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Bus was saved successfully.";

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Bus " + timeStamp;
        createPage.getName().setInputValue(name);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Bus: " + name, Optional.empty());

        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
