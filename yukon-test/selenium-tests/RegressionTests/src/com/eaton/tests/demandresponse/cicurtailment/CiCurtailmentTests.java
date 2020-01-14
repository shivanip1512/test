package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.CiCurtailmentPage;

public class CiCurtailmentTests extends SeleniumTestSetup {

    private CiCurtailmentPage curtailmentPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.DemandResponse.CI_CURTAILMENT);

        curtailmentPage = new CiCurtailmentPage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(curtailmentPage.getTitle(), "Commercial Curtailment");
    }
}
