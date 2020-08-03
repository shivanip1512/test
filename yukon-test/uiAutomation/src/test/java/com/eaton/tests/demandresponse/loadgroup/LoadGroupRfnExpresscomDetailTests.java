package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupRfnExpresscomDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscom_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Load Group: AT Load Group";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "664");
        
        LoadGroupDetailPage editPage = new LoadGroupDetailPage(driverExt, 664);

        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscom_copySuccess() {
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, 592);
        
        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied RFN Expresscom Ldgrp " + timeStamp;
        
        final String EXPECTED_MSG = name + " copied successfully.";
        
        modal.getName().setInputValue(name);
        
        modal.clickOkAndWaitForModalToClose();
        
        waitForPageToLoad("Load Group: " + name, Optional.of(8));
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, 592);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_deleteSuccess() {
        final String EXPECTED_MSG = "AT Delete RFN Expresscom Ldgrp deleted successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "593");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, 593);
        
        ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal();
        
        confirmModal.clickOkAndWaitForModalToClose();
        
        waitForPageToLoad("Setup", Optional.empty());
        
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }  
}
