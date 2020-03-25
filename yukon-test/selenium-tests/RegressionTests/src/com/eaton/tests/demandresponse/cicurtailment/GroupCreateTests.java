package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.GroupCreatePage;

public class GroupCreateTests extends SeleniumTestSetup {

    private GroupCreatePage createPage;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_GROUP_CREATE);

        createPage = new GroupCreatePage(driverExt);
    }

    @Test
    public void pageTitleCorrect() {        
        final String EXPECTED_TITLE = "Edit Group";

        String actualPageTitle = createPage.getPageTitle();

        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}