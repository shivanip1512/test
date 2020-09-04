package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterConstants;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterWRL420cLCreateTests extends SeleniumTestSetup {

    private AmiDashboardPage amiDashboardPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    private static final String CREATED = " created successfully.";
    private static final String METER = "Meter ";

    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Ami.AMI_DASHBOARD);

        amiDashboardPage = new AmiDashboardPage(driverExt);
        randomNum = getRandomNum();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cLCreate_allFieldsSuccess() {

        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(99999999);
        String manufacturer = MeterConstants.WRL420CL.getManufacturer();
        String model = MeterConstants.WRL420CL.getModel();
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT " + MeterConstants.WRL420CL.getMeterType() + " Meter " + timeStamp;
        createModal.getType().selectItemByTextSearch(MeterConstants.WRL420CL.getMeterType());
        createModal.getdeviceName().setInputValue(name);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(manufacturer);
        createModal.getModel().setInputValue(model);

        createModal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(METER + name + CREATED);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(amiDashboardPage);
    }
}
