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

public class MeterDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    
    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }     
    
    @Test(enabled = true, groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn530S4xSuccess() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-530S4x deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "588");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "588");
        
        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }
    
    @Test(enabled = true, groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn420flSuccess() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-420fL deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "584");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "584");
        
        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }
    
    @Test(enabled = true, groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn430Sl4Success() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-430SL4 deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "586");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "586");
        
        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }   
}
