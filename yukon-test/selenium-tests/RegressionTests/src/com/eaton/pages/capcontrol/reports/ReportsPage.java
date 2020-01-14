package com.eaton.pages.capcontrol.reports;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class ReportsPage extends PageBase {

    public ReportsPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

    }

    public String getTitle() {
        return driver.findElement(By.cssSelector(".title")).getText();
    }
}
