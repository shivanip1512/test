package com.eaton.framework.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public final class MyEdgeDriver {

    private MyEdgeDriver() {
    }

    public static WebDriver getNewEdgeDriver(boolean useRemoteDriver) {

        WebDriver driver;  
                
        //Hard coding edge driver for now since WebDriverManager is not working correctly
        //Change the location to point to where your local version of the edge driver is located
        System.setProperty("webdriver.edge.driver", "C:\\dev\\workspaces\\yukon\\yukon\\yukon-test\\uiAutomation\\SeleniumDrivers\\" + "msedgedriver.exe");
        
        //WebDriverManager.edgedriver().version("80.0.361.111").setup();
        
        EdgeOptions options = new EdgeOptions();                   

        if (useRemoteDriver) {
            driver = new RemoteWebDriver(options);
        } else
            driver = new EdgeDriver(options);
        
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        EventFiringWebDriver firingDriver = new EventFiringWebDriver(driver);                

        driver = firingDriver;

        return driver;
    }
}
