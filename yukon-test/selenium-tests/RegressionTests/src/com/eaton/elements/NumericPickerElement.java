package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NumericPickerElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement numPickerElement;

    public NumericPickerElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
        
        setNumericPicker();
    }
    
    public NumericPickerElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setNumericPicker();
    }
    
    public NumericPickerElement(WebDriver driver, String elementName, WebElement parentElement) {
        this.driver = driver;
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
            this.numPickerElement = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + elementName + "']"));
        } else if (this.parentElement != null) {
            this.numPickerElement =  this.parentElement.findElement(By.cssSelector("input[name='" + elementName + "']"));   
        } else {
            this.numPickerElement =  this.driver.findElement(By.cssSelector("input[name='" + elementName + "']"));   
        }        
    }
    
    public WebElement getNumericPicker() {
        return numPickerElement;
    }
}
