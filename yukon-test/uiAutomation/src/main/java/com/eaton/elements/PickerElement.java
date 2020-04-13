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
        this.driverExt.findElement(By.cssSelector("#" + this.id + " .b-label"), Optional.empty()).click();
    }  
    
    public String getLinkValue() {
        return this.driverExt.findElement(By.cssSelector(this.id + ".b-label"), Optional.empty()).getText();
    }
}
