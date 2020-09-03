package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserEditPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserListPage;

public class EnergyCompanyOperatorUserEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();   
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyOperatorUserEdit_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Operator User: automation";
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "212");
        
        EnergyCompanyOperatorUserEditPage page = new EnergyCompanyOperatorUserEditPage(driverExt, 64, 212);
                                 
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    } 

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyOperatorUserEdit_deleteOperatorUserSuccess() {
        final String EXPECTED_USER_MSG = "Successfully deleted the user.";
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "302");

        EnergyCompanyOperatorUserEditPage editPage = new EnergyCompanyOperatorUserEditPage(driverExt, 64, 302);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWaitForModalToClose();

        //waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_AFTER_DELETE_OR_EDIT, Optional.empty());

        EnergyCompanyOperatorUserListPage listPage = new EnergyCompanyOperatorUserListPage(driverExt, 64);

        String actualUserMsg = listPage.getUserMessage();

        assertThat(EXPECTED_USER_MSG).isEqualTo(actualUserMsg);
    }  
}
