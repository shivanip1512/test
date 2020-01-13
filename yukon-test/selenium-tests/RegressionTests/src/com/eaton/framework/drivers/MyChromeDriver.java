package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public final class MyChromeDriver {

    private MyChromeDriver() {
    }

    static WebDriver getNewChromeDriver(boolean useRemoteDriver, String driverLocation, boolean isHeadless) {

        WebDriver driver;

        System.setProperty("webdriver.chrome.driver", driverLocation);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");

        if (isHeadless)
            options.addArguments("headless");

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else {
            driver = new ChromeDriver(options);
        }

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);

        driver = firingDriver;

        return driver;
    }
}
