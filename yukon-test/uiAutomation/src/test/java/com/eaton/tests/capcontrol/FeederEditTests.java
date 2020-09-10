package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.FeederDetailPage;
import com.eaton.pages.capcontrol.FeederEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class FeederEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private FeederEditPage editPage;    

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.CapControl.FEEDER_EDIT + "668" + Urls.EDIT);
        editPage = new FeederEditPage(driverExt, 668);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(editPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void feederEdit_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Feeder: AT Feader";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void feederEdit_requiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Feeder was saved successfully.";

        navigate(Urls.CapControl.FEEDER_EDIT + "458" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Feeder " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Feeder: " + name, Optional.empty());

        FeederDetailPage detailsPage = new FeederDetailPage(driverExt, 458);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void feederEdit_deleteFeederSuccess() {
        final String EXPECTED_MSG = "Feeder AT Delete Feeder deleted successfully.";

        navigate(Urls.CapControl.FEEDER_EDIT + "575" + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}