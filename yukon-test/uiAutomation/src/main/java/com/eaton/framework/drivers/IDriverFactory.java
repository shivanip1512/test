package com.eaton.framework.drivers;

import org.openqa.selenium.WebDriver;

public interface IDriverFactory {

    WebDriver getWebDriver(String browser, Boolean useRemoveDriver, String url, Boolean headless);

}
