package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
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

public class MeterRfn420flCreateTests extends SeleniumTestSetup {

    private AmiDashboardPage amiDashboardPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    private static final String CREATED = " created successfully.";
    private static final String METER = "Meter ";
    
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Ami.AMI_DASHBOARD);

        amiDashboardPage = new AmiDashboardPage(driverExt);
        randomNum = getRandomNum();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Ami.AMI })
    public void meterRfn420flCreate_allFieldsSuccess() {
        CreateMeterModal createModal = amiDashboardPage.showAndWaitCreateMeterModal();

        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(99999999);
        String manufacturer = randomString(12);
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT RFN-420fL Meter " + timeStamp;
        createModal.getType().selectItemByTextSearch("RFN-420fL");
        createModal.getdeviceName().setInputValue(name);
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

    private String randomString(int length) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    @AfterMethod(alwaysRun=true)
    public void afterTest() {
        refreshPage(amiDashboardPage);
    }
}
