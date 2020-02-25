package com.eaton.tests.ami;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String PARENT_DIALOG_NAME = "yukon_dialog_confirm";
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";
    
    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_07_editRFNOjects" })
    public void editMeterRfn420flSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "492");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "492");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Edited RFN-420fL " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + UPDATED, "Expected User Msg: '" + METER + name + UPDATED + "' but found " + userMsg);
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_07_editRFNOjects" })
    public void editMeterRfn430Sl4Success() {
        navigate(Urls.Ami.METER_DETAIL + "586");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "586");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Edited RFN-430SL4 " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + UPDATED, "Expected User Msg: '" + METER + name + UPDATED + "' but found " + userMsg);
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_07_editRFNOjects" })
    public void editMeterRfn530S4xSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "587");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "587");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Edited RFN-530S4x " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + UPDATED, "Expected User Msg: '" + METER + name + UPDATED + "' but found " + userMsg);
    }
    
    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn530S4xSuccess() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-530S4x deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "588");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "588");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmModal modal = new ConfirmModal(driverExt, PARENT_DIALOG_NAME);
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }
    
    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn420flSuccess() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-420fL deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "584");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "584");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmModal modal = new ConfirmModal(driverExt, PARENT_DIALOG_NAME);
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }
    
    @Test(enabled = false, groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn430Sl4Success() {
        final String EXPECTED_MSG = "Meter AT Delete RFN-430SL4 deleted successfully.";
        
        navigate(Urls.Ami.METER_DETAIL + "786");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL + "786");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmModal modal = new ConfirmModal(driverExt, PARENT_DIALOG_NAME);
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI, Optional.of(10));
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg '" + EXPECTED_MSG + "' but found " + userMsg);
    }   
}
