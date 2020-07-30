package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class PickerElement {
    
    private DriverExtensions driverExt;
    private String id;

    public PickerElement(DriverExtensions driverExt, String id) {
        this.driverExt = driverExt;
        this.id = id;
    }
    
    public void clickLink() {
        this.driverExt.findElement(By.cssSelector("#" + this.id + " .b-label"), Optional.of(3)).click();
    }  
    
    public String getLinkValue() {
        return this.driverExt.findElement(By.cssSelector("#" +this.id + " .b-label"), Optional.of(3)).getText();
    }
    
    public String getValidationError() {
        return this.driverExt.findElement(By.cssSelector("span[id='deviceUsage.id.errors']"), Optional.of(3)).getText();
    }
}
