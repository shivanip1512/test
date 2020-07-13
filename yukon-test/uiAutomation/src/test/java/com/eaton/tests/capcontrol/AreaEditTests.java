package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.AreaDetailPage;
import com.eaton.pages.capcontrol.AreaEditPage;
import com.eaton.pages.capcontrol.CapControlDashboardPage;

public class AreaEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void areaEdit_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Area: AT Area";

        navigate(Urls.CapControl.AREA_EDIT + "672" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driverExt, 672);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void areaEdit_requiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Area was saved successfully.";

        navigate(Urls.CapControl.AREA_EDIT + "449" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driverExt, 449);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Area " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Area: " + name, Optional.empty());

        AreaDetailPage detailsPage = new AreaDetailPage(driverExt, 449);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void areaEdit_deleteSuccess() {
        final String EXPECTED_MSG = "Area AT Delete Area Deleted successfully.";

        navigate(Urls.CapControl.AREA_EDIT + "579" + Urls.EDIT);

        AreaEditPage editPage = new AreaEditPage(driverExt, 579);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWait();

        waitForUrlToLoad(Urls.CapControl.DASHBOARD, Optional.empty());

        CapControlDashboardPage detailsPage = new CapControlDashboardPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
