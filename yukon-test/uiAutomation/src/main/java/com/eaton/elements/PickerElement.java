package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class PickerElement {
    
    private DriverExtensions driverExt;
    private String dataPickerId;
    private String dynamicPickerId;

    public PickerElement(DriverExtensions driverExt, Optional<String> dataPickerId, Optional<String> dynamicPickerId) {
        this.driverExt = driverExt;
        
        if(dataPickerId.isPresent()) {
            this.dataPickerId = dataPickerId.get();
        }
        if(dynamicPickerId.isPresent()) {
            this.dynamicPickerId = dynamicPickerId.get();
        }
    }
    
    public void clickLink() {
        getPickerElement().findElement(By.cssSelector(".b-label")).click();
    }  
    
    public String getLinkValue() {
        return getPickerElement().findElement(By.cssSelector(".b-label")).getText();
    }    
    
    public String getValidationError(String deviceId) {
        return this.driverExt.findElement(By.cssSelector("span[id='" + deviceId + ".errors']"), Optional.of(3)).getText();
    }
    
    public WebElement getPickerElement() {
        if(dynamicPickerId != null) {
            return this.driverExt.findElement(By.cssSelector("[data-picker-id*='" + dynamicPickerId + "']"), Optional.of(3));
        } else {
            return this.driverExt.findElement(By.cssSelector("[data-picker-id='" + dataPickerId + "']"), Optional.of(3));
        }
    }    
}

