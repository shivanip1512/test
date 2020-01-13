package com.eaton.framework.drivers;

import org.openqa.selenium.WebDriver;

public class DriverFactory implements IDriverFactory {

    @Override
    public WebDriver getWebDriver(String browser, Boolean useRemoteDriver, String location, Boolean headless) {

        switch (browser.toUpperCase()) {
        case "CHROME":
            return MyChromeDriver.getNewChromeDriver(useRemoteDriver, location, headless);
        case "FIREFOX":
            return MyFirefoxDriver.getNewFirefoxDriver(useRemoteDriver, location, headless);
        case "EDGE":
            return MyEdgeDriver.getNewEdgeDriver(useRemoteDriver, location);
        case "INTERNETEXPLORER":
            return MyInternetExplorerDriver.getNewInternetExplorerDriver(useRemoteDriver, location);
        default:
            return MyChromeDriver.getNewChromeDriver(useRemoteDriver, location, headless);
        }
    }
}
