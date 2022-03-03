package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class TrueFalseCheckboxElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
    }
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName, String parentName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;        
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
        if(this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("input[name = '" + this.elementName + "']"), Optional.of(3));   
        }        
    }
}
