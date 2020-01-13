package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.GroupCreatePage;

public class GroupCreateTests extends SeleniumTestSetup {

    WebDriver driver;
    GroupCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.CI_GROUP_CREATE);

        this.createPage = new GroupCreatePage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Edit Group");
    }
}