package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterConstants;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterWRL420cLEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLEdit_nameOnlySuccess() {
    	final int DEVICEID = 1232;
        navigate(Urls.Ami.METER_DETAIL + DEVICEID);
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited " + MeterConstants.WRL420CL.getMeterType() + " " + timeStamp;

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, DEVICEID);

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        editModal.getdeviceName().setInputValue(name);
        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + DEVICEID, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, DEVICEID);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + name + UPDATED);
    }
}
