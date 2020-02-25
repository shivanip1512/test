package com.eaton.tests.ami;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterCreateTests extends SeleniumTestSetup {

    private AmiDashboardPage amiDashboardPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    private static final String CREATED = " created successfully.";
    private static final String METER = "Meter ";
    
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Ami.AMI);

        amiDashboardPage = new AmiDashboardPage(driverExt, Urls.Ami.AMI);

        randomNum = getRandomNum();
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_06_createRFNOjects" })
    public void createMeterRfn420flSuccess() {
        CreateMeterModal createModal = amiDashboardPage.showCreateMeterModal();

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

        createModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + CREATED, "Expected User Msg: 'Meter" + name + " created successfully.' but found: " + userMsg);
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_06_createRFNOjects" })
    public void createMeterRfn430Sl4Success() {
        CreateMeterModal createModal = amiDashboardPage.showCreateMeterModal();

        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(99999999);
        String manufacturer = randomString(12);
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT RFN-430SL4 Meter " + timeStamp;
        createModal.getType().selectItemByTextSearch("RFN-430SL4");
        createModal.getdeviceName().setInputValue(name);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(manufacturer);
        createModal.getModel().setInputValue("A3K");

        createModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + CREATED, "Expected User Msg: 'Meter" + name + " created successfully.' but found: " + userMsg);
    }
    
    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_06_createRFNOjects" })
    public void createMeterRfn530S4xSuccess() {

        CreateMeterModal createModal = amiDashboardPage.showCreateMeterModal();

        int meterNumber = randomNum.nextInt(999999);
        int serialNumber = randomNum.nextInt(99999999);
        String manufacturer = randomString(12);
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT RFN-430SL4 Meter " + timeStamp;
        createModal.getType().selectItemByTextSearch("RFN-530S4x");
        createModal.getdeviceName().setInputValue(name);
        createModal.getMeterNumber().setInputValue(String.valueOf(meterNumber));
        createModal.getSerialNumber().setInputValue(String.valueOf(serialNumber));
        createModal.getManufacturer().setInputValue(manufacturer);
        createModal.getModel().setInputValue("A3K");

        createModal.clickOk();

        waitForUrlToLoad(Urls.Ami.METER_DETAIL, Optional.of(10));

        MeterDetailsPage detailPage = new MeterDetailsPage(driverExt, Urls.Ami.METER_DETAIL);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, METER + name + CREATED, "Expected User Msg: 'Meter" + name + " created successfully.' but found: " + userMsg);
    }

    private String randomString(int length) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    @AfterMethod
    public void afterTest() {
        refreshPage(amiDashboardPage);
    }
}
