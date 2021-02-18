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
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterRfn420flEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private static final String METER = "Meter ";
    private static final String UPDATED = " updated successfully.";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.AMI })
    public void meterRfn420flEdit_RequiredFieldsOnly_Success() {
        String editRfn420FlId = TestDbDataType.MeterData.RFN_420FL_EDIT_ID.getId().toString();
        navigate(Urls.Ami.METER_DETAIL + editRfn420FlId);
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited RFN-420fL " + timeStamp;

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Integer.parseInt(editRfn420FlId));

        EditMeterModal editModal = meterDetailsPage.showMeterEditModal();

        editModal.getDeviceName().setInputValue(name);
        editModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL + 492, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Integer.parseInt(editRfn420FlId));

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + name + UPDATED);
    }
}
