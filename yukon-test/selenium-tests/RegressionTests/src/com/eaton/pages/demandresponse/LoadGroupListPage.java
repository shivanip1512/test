package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.WebTable;
import com.eaton.pages.PageBase;

public class LoadGroupListPage extends PageBase {

    private WebTable table;

    public LoadGroupListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

        table = new WebTable(driver, "compact-results-table");
    }

    public String getTitle() {

        return driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public WebTable getTable() {
        return table;
    }
}
