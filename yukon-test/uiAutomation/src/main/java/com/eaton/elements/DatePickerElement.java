package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.util.Strings;

import com.eaton.framework.DriverExtensions;

public class DatePickerElement {

    private DriverExtensions driverExt;
    private String elementName;

    public DatePickerElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public void setValue(String value) {
        WebElement picker = getPicker();

        picker.clear();
        picker.sendKeys(value);
    }

    public void clearValue() {
        getPicker().clear();
    }

    public WebElement getPicker() {
        return this.driverExt.findElement(By.cssSelector("input[name='" + elementName + "']"), Optional.of(3));
    }
    
    public boolean isPickerEnabled() {
        String disabled = getPicker().getAttribute("disabled");
        
        return Strings.isNullOrEmpty(disabled);               
    }
    
    public String getValue() {
        return getPicker().getAttribute("value");
    }
}