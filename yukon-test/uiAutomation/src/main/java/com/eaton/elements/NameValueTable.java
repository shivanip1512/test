package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class NameValueTable {

    private DriverExtensions driverExt;
    WebElement parentElement;
    
    public NameValueTable(DriverExtensions driverExt, WebElement parentElement) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;
    }
    
    private WebElement getTable() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector(".name-value-table"));
        } else {
            return this.driverExt.findElement(By.cssSelector(".name-value-table"), Optional.of(3)); 
        }
    }
    
    public Integer getLabelCount() {
        return getListOfLabelNames().size();
    }
    
    public Integer getValueCount() {
        return getListOfValues().size();
    }
    
    private List<WebElement> getListOfLabelNames() {
        return getTable().findElements(By.cssSelector(".name"));
    }
    
    private List<WebElement> getListOfValues() {
        return getTable().findElements(By.cssSelector(".value"));
    }
    
    public String getLabelByRow(Integer index) {
        List<WebElement> labels = getListOfLabelNames();
        
        return labels.get(index).getText();
    }
    
    public String getValueByRow(Integer index) {
        String label;
        List<WebElement> labels = getListOfValues();
        
        long startTime = System.currentTimeMillis();   
        
        do {
            label = labels.get(index).getText();
        } while(label.contains("loading") && ((System.currentTimeMillis() - startTime) < 2000));
        
        return labels.get(index).getText();
    }
}
