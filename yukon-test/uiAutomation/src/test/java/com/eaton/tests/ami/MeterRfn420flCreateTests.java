package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;
import com.github.javafaker.Faker;

public class MeterRfn420flCreateTests extends SeleniumTestSetup {

    private AmiDashboardPage page;
    private DriverExtensions driverExt;
    private Faker faker;
    private static final String CREATED = " created successfully.";
    private static final String METER = "Meter ";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();

        navigate(Urls.Ami.AMI_DASHBOARD);
        page = new AmiDashboardPage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if (getRefreshPage()) {
            refreshPage(page);
        }
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.AMI })
    public void meterRfn420flCreate_AllFields_Success() {
        CreateMeterModal createModal = page.showAndWaitCreateMeterModal();

        int meterNumber = faker.number().numberBetween(1, 999999);
        int serialNumber = faker.number().numberBetween(1, 99999999);
        String manufacturer = faker.company().industry();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT RFN-420fL Meter " + timeStamp;
        createModal.getType().selectItemByTextSearch("RFN-420fL");
        createModal.getDeviceName().setInputValue(name);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(manufacturer);
        createModal.getModel().setInputValue("A3K");

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(METER + name + CREATED).isEqualTo(userMsg);
    }
}
