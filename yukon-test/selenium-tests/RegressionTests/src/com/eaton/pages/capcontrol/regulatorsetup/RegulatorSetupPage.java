package com.eaton.pages.capcontrol.regulatorsetup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class RegulatorSetupPage extends PageBase {

    public RegulatorSetupPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
