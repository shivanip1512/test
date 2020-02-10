package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TimePickerElement {

    private WebDriver driver;
    private String elementName;

    public TimePickerElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
    }
    
    public void setValue(String value) {
        WebElement picker = getNumericPicker();
        
        picker.clear();
        picker.sendKeys(value);
    }
    
    public void clearValue() {
        getNumericPicker().clear();
    }
 
    public WebElement getNumericPicker() {
        return this.driver.findElement(By.cssSelector("input[name='" + elementName + "']"));
    }
}
