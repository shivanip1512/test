package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class LoadGroupDetailPage extends PageBase {

    public LoadGroupDetailPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public String getUserMessageSuccess() {
        return this.driver.findElement(By.cssSelector(".yukon-content .user-message.success")).getText();
    }
}