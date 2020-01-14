package com.eaton.pages.capcontrol.pointimport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class PointImportPage extends PageBase {

    public PointImportPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public String getTitle() {
        return driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
