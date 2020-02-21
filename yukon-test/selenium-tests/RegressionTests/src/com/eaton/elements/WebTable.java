package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class WebTable {

    private DriverExtensions driverExt;
    private String tableClassName;
    private List<WebTableColumnHeader> columnHeaders = null;
    private List<WebTableRow> dataRows;
    private WebElement parentElement; 

    public WebTable(DriverExtensions driverExt, String tableClassName) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
    }
    
    public WebTable(DriverExtensions driverExt, String tableClassName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parentElement = parentElement;
    }
    
    private WebElement getTable() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("." + this.tableClassName));
        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.tableClassName), Optional.empty());   
        } 
    }

    public List<WebTableColumnHeader> getColumnHeaders() {

        if (this.columnHeaders == null) {
            findColumnHeaders();
        }

        return this.columnHeaders;
    }

    public List<WebTableRow> getDataRows() {

        findDataRows();

        return this.dataRows;
    }      

    public void waitForLoadToComplete() {
        // TODO determine how to wait for table to finish loading...
    }
    
    public void filterTable(String filterCriteria) {        
        filter().enterFilterCritera(filterCriteria);        
    }
    
    public void clearFilter() {
        filter().clearFilterCriteria();
    }
    
    public void searchTable(String value) {
        TextEditElement search = new TextEditElement(this.driverExt, "ss");
        
        search.clearInputValue();
        search.setInputValue(value);
        
        waitForLoadToComplete();
    }
    
    public void clearSearch() {
        TextEditElement search = new TextEditElement(this.driverExt, "ss");
        search.clearInputValue();
    }
    
    private WebTableFilter filter() {
        WebElement filterElement = this.driverExt.findElement(By.cssSelector(".filter-section"), Optional.empty());
        
        return new WebTableFilter(filterElement, this.driverExt);
    }

    private void findDataRows() {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));

        List<WebTableRow> newList = new ArrayList<>();
        for (WebElement element : rowList) {

            newList.add(new WebTableRow(element));
        }
    }
    
    public WebTableRow getDataRowByName(String name) {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));
        
        //WebElement element = rowList.stream().filter((row) -> row.findElement(By.cssSelector("a")).getText().contains(name)).findFirst().orElseThrow();
        
        //return new WebTableRow(element);
       
        for (WebElement row : rowList) {
            String text = row.findElement(By.cssSelector("a")).getText();
            
            if (text.equals(name)) {
                return new WebTableRow(row);
            }
        }
        return null;
    }    

    private void findColumnHeaders() {

        List<WebElement> headerList = this.getTable().findElements(By.cssSelector("thead tr th"));

        this.columnHeaders = new ArrayList<>();
        for (WebElement element : headerList) {

            this.columnHeaders.add(new WebTableColumnHeader(element));
        }
    }
}
