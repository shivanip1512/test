package com.eaton.tests.demandresponse.cicurtailment;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.cicurtailment.GroupListPage;

public class GroupListTests extends SeleniumTestSetup {

    WebDriver driver;
    GroupListPage listPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.CI_GROUP_LIST);

        this.listPage = new GroupListPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DrCICurtailmentNavigation" })
    public void titleCorrect() {

        Assert.assertEquals(this.listPage.getTitle(), "Groups");
    }
}