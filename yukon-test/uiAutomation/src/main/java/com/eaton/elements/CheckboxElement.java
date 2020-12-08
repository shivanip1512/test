package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class CheckboxElement {

    private DriverExtensions driverExt;
    private String elementID;
    
    public CheckboxElement(DriverExtensions driverExt, String elementID) {        
        this.driverExt = driverExt;
        this.elementID = elementID;
    }
    
    public void setValue(Boolean value) {
        
        Boolean checked = isChecked();
        
        if (!value.equals(checked)) {
            getCheckbox().click();
        }        
    }
    
    private Boolean isChecked() {
         String checked = getCheckbox().getAttribute("checked");
         
         return checked == null ? false : checked.equals("checked");
    }
    
    private WebElement getCheckbox() {
    	return this.driverExt.findElement(By.id(this.elementID), Optional.of(3));

    }        
    
}