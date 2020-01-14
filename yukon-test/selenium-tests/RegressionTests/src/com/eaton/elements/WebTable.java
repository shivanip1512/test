package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebTable {

    WebDriver driver;
    private String tableClassName;
    private List<ColumnHeader> columnHeaders = null;
    private List<WebRow> dataRows;

    public WebTable(WebDriver driver, String tableClassName) {
        driver = driver;
        tableClassName = tableClassName;
        getTable();
    }

    private WebElement getTable() {
        return driver.findElement(By.cssSelector("." + this.tableClassName));
    }

    public List<ColumnHeader> getColumnHeaders() {

        if (columnHeaders == null) {
            findColumnHeaders();
        }

        return columnHeaders;
    }

    public List<WebRow> dataRow() {

        findDataRows();

        return dataRows;
    }

    public void waitForLoadToComplete() {
        // determine how to wait for table to finish loading...
    }

    private void findDataRows() {
        List<WebElement> rowList = getTable().findElements(By.cssSelector("tbody tr"));

        List<WebRow> newList = new ArrayList<>();
        for (WebElement element : rowList) {

            newList.add(new WebRow(element));
        }
    }

    private void findColumnHeaders() {

        List<WebElement> headerList = getTable().findElements(By.cssSelector("thead tr th"));

        columnHeaders = new ArrayList<>();
        for (WebElement element : headerList) {

            columnHeaders.add(new ColumnHeader(element));
        }
    }
}
