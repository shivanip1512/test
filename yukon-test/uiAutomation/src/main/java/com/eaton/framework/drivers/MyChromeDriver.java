package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public final class MyChromeDriver {

    private MyChromeDriver() {
    }

    static WebDriver getNewChromeDriver(boolean useRemoteDriver, boolean isHeadless, String proxy, Boolean useProxy) {

        WebDriver driver;
        if(useProxy)
            WebDriverManager.chromedriver().proxy(proxy).setup();
        else
            WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");

        if (isHeadless)
            options.addArguments("headless");

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else {
            driver = new ChromeDriver(options);
        }

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30L, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(3L, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);

        driver = firingDriver;

        return driver;
    }
}
