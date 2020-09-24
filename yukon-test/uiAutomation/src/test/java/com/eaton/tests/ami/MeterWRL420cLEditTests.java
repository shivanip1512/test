package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterEnums;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.WRL420cLMeterDetailsPage;

public class MeterWRL420cLEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private WRL420cLMeterDetailsPage meterDetailsPage;
    private static final int DEVICEID = 1232;
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Ami.METER_DETAIL + DEVICEID);
        
        meterDetailsPage = new WRL420cLMeterDetailsPage(driverExt, DEVICEID);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(meterDetailsPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.AMI })
    public void meterWRL420cLEdit_RequiredFieldsOnly_Success() {

        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited " + MeterEnums.MeterType.WRL420CL.getMeterType() + " " + timeStamp;

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        editModal.getdeviceName().setInputValue(name);
        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + DEVICEID, Optional.of(10));

        WRL420cLMeterDetailsPage detailPage = new WRL420cLMeterDetailsPage(driverExt, DEVICEID);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + name + UPDATED);
    }
}
