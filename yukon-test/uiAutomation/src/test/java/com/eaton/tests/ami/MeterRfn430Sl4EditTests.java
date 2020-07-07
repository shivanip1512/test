package com.eaton.tests.ami;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterRfn430Sl4EditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }  

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Ami.AMI })
    public void editMeterRfn430Sl4Success() {
        navigate(Urls.Ami.METER_DETAIL + "585");
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited RFN-430SL4 " + timeStamp;

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, 585);

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        editModal.getdeviceName().setInputValue(name);
        editModal.clickOkAndWait();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, 585);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + UPDATED, "Expected User Msg: '" + METER + name + UPDATED + "' but found " + userMsg);
    }    
}

