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
    private WebElement checkbox;
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        
        setCheckbox();
    }
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName, String parentName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setCheckbox();
    }
    
    public TrueFalseCheckboxElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;        
        
        setCheckbox();
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
    
    private void setCheckbox() {
        if(this.parentName != null) {
            this.checkbox = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            this.checkbox = this.parentElement.findElement(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            this.checkbox = this.driverExt.findElement(By.cssSelector("input[name = '" + this.elementName + "']"), Optional.empty());   
        }        
    }
    
    private WebElement getCheckbox() {
        return checkbox;
    }
}
