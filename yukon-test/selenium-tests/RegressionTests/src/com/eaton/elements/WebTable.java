package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebTable {

    private WebDriver driver;
    private String tableClassName;
    private List<WebTableColumnHeader> columnHeaders = null;
    private List<WebTableRow> dataRows;
    public WebTableFilter filter;    
    public WebElement table;

    public WebTable(WebDriver driver, String tableClassName) {
        this.driver = driver;
        this.tableClassName = tableClassName;
        setTable();
    }
    
    private void setTable() {
        this.table = this.driver.findElement(By.cssSelector("." + this.tableClassName));
    }

    private WebElement getTable() {
        return this.table;
    }

    public List<WebTableColumnHeader> getColumnHeaders() {

        if (this.columnHeaders == null) {
            findColumnHeaders();
        }

        return this.columnHeaders;
    }

    public List<WebTableRow> getDataRow() {

        findDataRows();

        return this.dataRows;
    }    

    public void waitForLoadToComplete() {
        // TODO determine how to wait for table to finish loading...
    }
    
    public void filterTable(String filter) {
        filter().enterFilterCritera(filter);
        //TODO add code to wait for table to filter
    }
    
    public void clearFilter() {
        filter().clearFilterCriteria();
        //TODO add code to wait for table to filter
    }
    
    private WebTableFilter filter() {
        WebElement filterElement = this.driver.findElement(By.cssSelector(".filter-section"));
        
        return new WebTableFilter(filterElement, this.driver);
    }

    private void findDataRows() {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));

        List<WebTableRow> newList = new ArrayList<>();
        for (WebElement element : rowList) {

            newList.add(new WebTableRow(element));
        }
    }

    private void findColumnHeaders() {

        List<WebElement> headerList = this.getTable().findElements(By.cssSelector("thead tr th"));

        this.columnHeaders = new ArrayList<>();
        for (WebElement element : headerList) {

            this.columnHeaders.add(new WebTableColumnHeader(element));
        }
    }
}
