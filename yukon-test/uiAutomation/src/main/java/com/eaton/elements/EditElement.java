package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class EditElement {

    protected DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement inputElement;

    public EditElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;     
        
        setEditElement();
    }
    
    public EditElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setEditElement();
    }  
    
    public EditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setEditElement();
    }

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.empty());

        return !list.isEmpty() ? true : false;
    }

    protected void setEditElement() {
        if (this.parentName != null) {
            this.inputElement = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            this.inputElement = this.parentElement.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
        } else {
            this.inputElement = this.driverExt.findElement(By.cssSelector("input[name='" + this.elementName + "']"), Optional.empty());
        }        
    }
    
    protected WebElement getEditElement() {
        return this.inputElement;
    }
}
