package com.eaton.pages.demandresponse.cicurtailment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class GroupCreatePage extends PageBase {

    public GroupCreatePage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

    }

    public String getTitle() {

        return driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
