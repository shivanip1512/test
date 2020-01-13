package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public final class MyInternetExplorerDriver {

    private MyInternetExplorerDriver() {

    }

    @SuppressWarnings("deprecation")
    public static WebDriver getNewInternetExplorerDriver(boolean useRemoteDriver, String driverLocation) {
        WebDriver driver;

        System.setProperty("webdriver.ie.driver", driverLocation);

        InternetExplorerOptions options = new InternetExplorerOptions();

        options.introduceFlakinessByIgnoringSecurityDomains();
        options.enablePersistentHovering();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        options.disableNativeEvents();
        options.ignoreZoomSettings();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.enableNativeEvents();
        options.requireWindowFocus();

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else
            driver = new InternetExplorerDriver(options);

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);

        driver = firingDriver;

        return driver;
    }
}
