package com.eaton.framework.drivers;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public final class MyEdgeDriver {

    private MyEdgeDriver() {
    }

    public static WebDriver getNewEdgeDriver(boolean useRemoteDriver, String proxy, Boolean useProxy) {
        WebDriver driver;
        
        if(useProxy)
            WebDriverManager.edgedriver().proxy(proxy).setup();
        else
            WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();   
        options.setCapability("useAutomationExtension", false);
        options.setCapability("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setCapability( "disable-infobars", true);

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else
            driver = new EdgeDriver(options);
        
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);                

        driver = firingDriver;

        return driver;
    }
}
