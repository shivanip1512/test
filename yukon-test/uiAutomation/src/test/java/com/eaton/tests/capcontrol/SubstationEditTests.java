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
import com.eaton.pages.capcontrol.SubstationDetailPage;
import com.eaton.pages.capcontrol.SubstationEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private SubstationEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.CapControl.SUBSTATION_EDIT + "666" + Urls.EDIT);
        editPage = new SubstationEditPage(driverExt, 666);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(editPage);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationEdit_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Substation: AT Substation";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationEdit_requiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Substation was saved successfully.";

        navigate(Urls.CapControl.SUBSTATION_EDIT + "451" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Substation " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Substation: " + name, Optional.empty());

        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt, 451);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationEdit_deleteSubstationSuccess() {
        final String EXPECTED_MSG = "Substation AT Delete Substation deleted successfully.";

        navigate(Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
