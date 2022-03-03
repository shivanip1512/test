package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class NameValueTable {

    private DriverExtensions driverExt;
    WebElement parentElement;
    Integer index;
    
    /**
     * @param driverExt - pass in the driveExt so can select elements
     * @param parentElement - the parent element of the table
     * @param index - this is optional, use if there are multiple name value tables for a single parent
     */
    public NameValueTable(DriverExtensions driverExt, WebElement parentElement, Optional<Integer> index) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;
        
        this.index = index.orElse(0);
    }
    
    private WebElement getTable() {
        List<WebElement> list;
        
        if (this.parentElement != null) {
            list = this.parentElement.findElements(By.cssSelector(".name-value-table"));
        } else {
            list = this.driverExt.findElements(By.cssSelector(".name-value-table"), Optional.of(3));
        }
        
        return list.get(this.index);
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
        } while(label.contains("loading") && ((System.currentTimeMillis() - startTime) < 5000));
        
        return label.trim();
    }
}
