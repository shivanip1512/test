package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.ProgramsPage;

public class ProgramsTests extends SeleniumTestSetup {

    private ProgramsPage programPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_PROGRAM_LIST);

        programPage = new ProgramsPage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(programPage.getTitle(), "Programs");
    }
}