package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.EditMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterRfn530S4xEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.Ami.AMI })
    public void meterRfn530S4xEdit_RequiredFieldsOnly_Success() {
        navigate(Urls.Ami.METER_DETAIL + "587");
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited RFN-530S4x " + timeStamp;

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, 587);

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        editModal.getdeviceName().setInputValue(name);
        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + 587, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, 587);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + name + UPDATED);
    }
}
