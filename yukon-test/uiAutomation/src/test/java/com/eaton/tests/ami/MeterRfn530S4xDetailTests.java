package com.eaton.tests.ami;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterRfn530S4xDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }     
    
    @Test(enabled = true, groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Ami.AMI })
    public void deleteMeterRfn530S4xSuccess() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-530S4x deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "588");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, 588);
        
        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }        
}

