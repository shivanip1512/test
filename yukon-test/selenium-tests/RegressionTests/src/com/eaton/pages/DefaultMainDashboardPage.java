package com.eaton.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DefaultMainDashboardPage extends PageBase {

    public DefaultMainDashboardPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public String getTitle() {
        return driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
