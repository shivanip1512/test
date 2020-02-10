package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NumericPickerElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;

    public NumericPickerElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public void setValue(int value) {
        WebElement picker = getNumericPicker();
        
        picker.clear();
        picker.sendKeys(Integer.toString(value));
    }
    
    public void clearValue() {
        getNumericPicker().clear();
    }
 
    public WebElement getNumericPicker() {
        if (parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("input[name='" + elementName + "']"));   
        }        
    }
}
