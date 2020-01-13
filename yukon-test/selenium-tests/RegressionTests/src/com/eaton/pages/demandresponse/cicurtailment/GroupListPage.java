package com.eaton.pages.demandresponse.cicurtailment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.pages.PageBase;

public class GroupListPage extends PageBase {

    public GroupListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
}
