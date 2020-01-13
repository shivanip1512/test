package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.WebTable;
import com.eaton.pages.PageBase;

public class ControlAreaListPage extends PageBase {

    private WebTable table;

    public ControlAreaListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);

        setTable(new WebTable(driver, "compact-results-table"));
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
