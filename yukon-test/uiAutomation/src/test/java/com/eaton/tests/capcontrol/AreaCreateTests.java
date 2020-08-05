package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.framework.test.annotation.CustomTestNgAnnotations;
import com.eaton.pages.capcontrol.AreaCreatePage;
import com.eaton.pages.capcontrol.AreaDetailPage;

public class AreaCreateTests extends SeleniumTestSetup {

    private AreaCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        navigate(Urls.CapControl.AREA_CREATE);

        this.createPage = new AreaCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void areaCreate_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Area";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    @CustomTestNgAnnotations(refreshPage = true, urlToRefresh = Urls.CapControl.AREA_CREATE)
    public void areaCreate_RequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Area was saved successfully.";

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Area " + timeStamp;
        this.createPage.getName().setInputValue(name);

        this.createPage.getSaveBtn().click();

        waitForPageToLoad("Area: " + name, Optional.empty());

        AreaDetailPage detailsPage = new AreaDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        this.createPage = new AreaCreatePage(driverExt);
    }
}
