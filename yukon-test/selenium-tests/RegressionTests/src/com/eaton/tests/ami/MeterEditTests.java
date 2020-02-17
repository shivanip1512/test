package com.eaton.tests.ami;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmDeleteModal;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterEditTests extends SeleniumTestSetup {

    private WebDriver driver;
    
    @BeforeClass
    public void beforeClass() {
        this.driver = getDriver();                
    }

    @Test(groups = { "smoketest", "SM03_07_editRFNOjects" })
    public void editMeterRfn420flSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "492");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "492");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited RFN-420fL " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL);

        MeterDetailsPage detailPage = new MeterDetailsPage(driver, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, "Meter " + name + " updated successfully.");
    }

    @Test(groups = { "smoketest", "SM03_07_editRFNOjects" })
    public void editMeterRfn430Sl4Success() {
        navigate(Urls.Ami.METER_DETAIL + "586");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "586");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited RFN-430SL4 " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL);

        MeterDetailsPage detailPage = new MeterDetailsPage(driver, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, "Meter " + name + " updated successfully.");
    }

    @Test(groups = { "smoketest", "SM03_07_editRFNOjects" })
    public void editMeterRfn530S4xSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "587");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "587");

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited RFN-530S4x " + timeStamp;
        editModal.getdeviceName().setInputValue(name);
        editModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL);

        MeterDetailsPage detailPage = new MeterDetailsPage(driver, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, "Meter " + name + " updated successfully.");
    }
    
    @Test(enabled = false, groups = { "smoketest", "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn530S4xSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "588");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "588");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI);
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(this.driver, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Meter AT Delete RFN-530S4x deleted successfully.");
    }
    
    @Test(enabled = false, groups = { "smoketest", "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn420flSuccess() {
        navigate(Urls.Ami.METER_DETAIL + "584");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "584");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI);
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(this.driver, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Meter AT Delete RFN-420fL deleted successfully.");
    }
    
    @Test(enabled = false, groups = { "smoketest", "SM03_08_deleteRFNOjects" })
    public void deleteMeterRfn430Sl4Success() {
        navigate(Urls.Ami.METER_DETAIL + "786");

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(this.driver, Urls.Ami.METER_DETAIL + "786");
        
        meterDetailsPage.getAction().clickAndSelectOptionByText("Delete Meter");
        
        ConfirmDeleteModal modal = new ConfirmDeleteModal(this.driver, "yukon_dialog_confirm");
        
        modal.clickOk();
        
        waitForUrlToLoad(Urls.Ami.AMI);
        
        AmiDashboardPage dashboardPage = new AmiDashboardPage(this.driver, Urls.Ami.AMI);
        
        String userMsg = dashboardPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Meter AT Delete RFN-430SL4 deleted successfully.");
    }   
}
