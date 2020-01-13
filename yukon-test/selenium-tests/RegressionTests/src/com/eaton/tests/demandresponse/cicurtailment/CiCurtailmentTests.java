package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.CiCurtailmentPage;

public class CiCurtailmentTests extends SeleniumTestSetup {

    WebDriver driver;
    CiCurtailmentPage curtailmentPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.CI_CURTAILMENT);

        this.curtailmentPage = new CiCurtailmentPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(this.curtailmentPage.getTitle(), "Commercial Curtailment");
    }
}
