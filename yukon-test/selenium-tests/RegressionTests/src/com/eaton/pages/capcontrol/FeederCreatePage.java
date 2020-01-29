package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class FeederCreatePage extends PageBase {

    public FeederCreatePage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
