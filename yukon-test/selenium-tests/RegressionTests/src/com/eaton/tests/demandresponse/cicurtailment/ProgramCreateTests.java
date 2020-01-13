package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.ProgramCreatePage;

public class ProgramCreateTests extends SeleniumTestSetup {

    WebDriver driver;
    ProgramCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.CI_PROGRAM_CREATE);

        this.createPage = new ProgramCreatePage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create Program");
    }
}