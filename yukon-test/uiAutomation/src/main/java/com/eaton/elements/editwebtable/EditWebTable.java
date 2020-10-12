package com.eaton.elements.editwebtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class EditWebTable {
    
    private DriverExtensions driverExt;
    private String tableClassName;
    private List<EditWebTableColumnHeader> columnHeaders = null;
    private WebElement parentElement; 
    private String parent;

    public EditWebTable(DriverExtensions driverExt, String tableClassName) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
    }    
    
    public EditWebTable(DriverExtensions driverExt, String tableClassName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parentElement = parentElement;        
    }
    
    public EditWebTable(DriverExtensions driverExt, String tableClassName, String parent) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parent = parent;
    }
    
    private WebElement getTable() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("table." + this.tableClassName));
        } else if (this.parent != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby*='" + parent + "'] table." + this.tableClassName), Optional.of(3));   

        } else {
            return this.driverExt.findElement(By.cssSelector("table." + this.tableClassName), Optional.of(3)); 
        }
    }

    private List<EditWebTableColumnHeader> getColumnHeaders() {

        if (this.columnHeaders == null) {
            findColumnHeaders();
        }

        return this.columnHeaders;
    }
    
    public List<String> getListTableHeaders() {
        List<EditWebTableColumnHeader> headers = getColumnHeaders();
        
        List<String> headerList = new ArrayList<>();

        for (EditWebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }
        
        return headerList;
    }
    
    private void findColumnHeaders() {

        List<WebElement> headerList = this.getTable().findElements(By.cssSelector("tr th"));

        this.columnHeaders = new ArrayList<>();
        for (WebElement element : headerList) {

            this.columnHeaders.add(new EditWebTableColumnHeader(element));
        }
    }    
    
    public EditWebTableRow getDataRowByName(String name) {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));
        
        WebElement element = rowList.stream().filter(x -> x.findElement(By.cssSelector("td:nth-child(1) span")).getText().contains(name)).findFirst().orElseThrow();
        
        return new EditWebTableRow(this.driverExt, element);
    } 
}
