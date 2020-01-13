package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public final class MyFirefoxDriver {

    private MyFirefoxDriver() {
    }

    static WebDriver getNewFirefoxDriver(boolean useRemoteDriver, String driverLocation, boolean isHeadless) {

        WebDriver driver;

        System.setProperty("webdriver.gecko.driver", driverLocation);

        FirefoxOptions options = new FirefoxOptions();

        if (isHeadless)
            options.addArguments("-headless");

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else {
            driver = new FirefoxDriver(options);
        }

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);

        driver = firingDriver;

        return driver;
    }
}
