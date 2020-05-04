package com.eaton.framework.drivers;

import org.openqa.selenium.WebDriver;

public class DriverFactory implements IDriverFactory {

    @Override
    public WebDriver getWebDriver(String browser, Boolean useRemoteDriver, Boolean headless) {

        switch (browser.toUpperCase()) {
        case "CHROME":
            return MyChromeDriver.getNewChromeDriver(useRemoteDriver, headless);
        case "FIREFOX":
            return MyFirefoxDriver.getNewFirefoxDriver(useRemoteDriver, headless);
        case "EDGE":
            return MyEdgeDriver.getNewEdgeDriver(useRemoteDriver);
        case "IE":
            return MyInternetExplorerDriver.getNewInternetExplorerDriver(useRemoteDriver);
        default:
            return MyFirefoxDriver.getNewFirefoxDriver(useRemoteDriver, headless);
        }
    }
}
