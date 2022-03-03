package com.eaton.framework.drivers;

import org.openqa.selenium.WebDriver;

public class DriverFactory implements IDriverFactory {

    @Override
    public WebDriver getWebDriver(String browser, Boolean useRemoteDriver, Boolean headless ,String proxy, Boolean useProxy) {

        switch (browser.toUpperCase()) {
        case "CHROME":
            return MyChromeDriver.getNewChromeDriver(useRemoteDriver, headless, proxy, useProxy);
        case "FIREFOX":
            return MyFirefoxDriver.getNewFirefoxDriver(useRemoteDriver, headless, proxy, useProxy);
        case "EDGE":
            return MyEdgeDriver.getNewEdgeDriver(useRemoteDriver, proxy, useProxy);
        case "IE":
            return MyInternetExplorerDriver.getNewInternetExplorerDriver(useRemoteDriver);
        default:
            return MyChromeDriver.getNewChromeDriver(useRemoteDriver, headless, proxy, useProxy);
        }
    }
}
