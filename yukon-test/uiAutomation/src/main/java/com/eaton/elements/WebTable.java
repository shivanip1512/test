package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class WebTable {

    private DriverExtensions driverExt;
    private String tableClassName;
    private List<WebTableColumnHeader> columnHeaders = null;
    private WebElement parentElement; 
    private String parent;

    public WebTable(DriverExtensions driverExt, String tableClassName) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
    }    
    
    public WebTable(DriverExtensions driverExt, String tableClassName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parentElement = parentElement;        
    }
    
    public WebTable(DriverExtensions driverExt, String tableClassName, String parent) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parent = parent;
    }
    
    private WebElement getTable() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("." + this.tableClassName));
        } else if (this.parent != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby*='" + parent + "'] ." + this.tableClassName), Optional.of(3));   

        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.tableClassName), Optional.of(3)); 
        }
    }

    private List<WebTableColumnHeader> getColumnHeaders() {

        if (this.columnHeaders == null) {
            findColumnHeaders();
        }

        return this.columnHeaders;
    }
    
    public List<String> getListTableHeaders() {
        List<WebTableColumnHeader> headers = getColumnHeaders();
        
        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }
        
        return headerList;
    }
    
    public List<String> getDataRowsTextByCellIndex(int index) {
        List<WebTableRow> rows = getDataRows();
        
        List<String> cellRowsData = new ArrayList<>();
        
        for (WebTableRow row : rows) {
            WebElement cell = row.getCell(index);
            cellRowsData.add(cell.getText());
        }
        
        return cellRowsData;
    }

    private void waitForSearch() {
        WebElement table = null;
        List<WebElement> rows = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        while((rows.size() != 1) && (System.currentTimeMillis() - startTime) < 500) {
            try {
                table = this.driverExt.findElement(By.cssSelector(".compact-results-table"), Optional.empty());

                rows = table.findElements(By.cssSelector("tbody tr"));  
            } 
            catch(StaleElementReferenceException ex) {
            }
        }        
    }
    
    private void waitForSearch(WebElement parent) {
        WebElement table = null;
        List<WebElement> rows = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        while((rows.size() != 1) && (System.currentTimeMillis() - startTime) < 3000) {
            try {
                table = parent.findElement(By.cssSelector(".compact-results-table"));

                rows = table.findElements(By.cssSelector("tbody tr"));  
            } 
            catch(StaleElementReferenceException ex) {
            }
        }        
    }    
    
    public void searchTable(String value) {
        
        if (parentElement != null) {
            TextEditElement search = new TextEditElement(this.driverExt, "ss", parentElement);
            
            search.setInputValue(value);
            
            waitForSearch();
        } else if (parent != null) {
            TextEditElement search = new TextEditElement(this.driverExt, "ss", parent);
            
            search.setInputValue(value);
            
            waitForSearch();
        } else {
            TextEditElement search = new TextEditElement(this.driverExt, "ss");  
            
            search.setInputValue(value);
            
            waitForSearch();
        }              
    }
    
    public void searchTable(String value, WebElement parent) {
        TextEditElement search = new TextEditElement(this.driverExt, "ss", parent);
        
        search.setInputValue(value);
        
        waitForSearch(parent);
    }
    
    public void clearSearch() {
        TextEditElement search = new TextEditElement(this.driverExt, "ss");
        search.clearInputValue();
    }    

    private List<WebTableRow> getDataRows() {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));

        List<WebTableRow> newList = new ArrayList<>();
        for (WebElement element : rowList) {

            newList.add(new WebTableRow(element));
        }
        
        return newList;
    }
    
    public WebTableRow getDataRowByName(String name) {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));
        
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
    
    public String getTableMessage() {
        return this.driverExt.findElement(By.cssSelector(".empty-list"), Optional.of(2)).getText();
    }
}
