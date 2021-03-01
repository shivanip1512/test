package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public final class MyFirefoxDriver {

    private MyFirefoxDriver() {
    }

    static WebDriver getNewFirefoxDriver(boolean useRemoteDriver, boolean isHeadless, String proxy, Boolean useProxy) {

        WebDriver driver;

        if(useProxy)
            WebDriverManager.chromedriver().proxy(proxy).setup();
        else
            WebDriverManager.chromedriver().setup();

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
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);

        driver = firingDriver;

        return driver;
    }
}
