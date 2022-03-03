package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class NumericPickerElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement numPickerElement;

    public NumericPickerElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        
        setNumericPicker();
    }
    
    public NumericPickerElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setNumericPicker();
    }
    
    public NumericPickerElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setNumericPicker();
    }
    
    public void setValue(int value) {
        WebElement picker = getNumericPicker();
        
        picker.clear();
        picker.sendKeys(Integer.toString(value));
    }
    
    public void clearValue() {
        getNumericPicker().clear();
    }
 
    public void setNumericPicker() {
        if (this.parentName != null) {
            this.numPickerElement = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            this.numPickerElement =  this.parentElement.findElement(By.cssSelector("input[name='" + elementName + "']"));   
        } else {
            this.numPickerElement =  this.driverExt.findElement(By.cssSelector("input[name='" + elementName + "']"), Optional.of(3));   
        }        
    }
    
    public WebElement getNumericPicker() {
        return numPickerElement;
    }
}
