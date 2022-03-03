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

    public EditElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;     
    }
    
    public EditElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }  
    
    public EditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(5));

        return !list.isEmpty() ? true : false;
    }

    public WebElement getEditElement() {
        if (this.parentName != null) {
            this.driverExt.waitUntilElementVisibleByCssLocator("[aria-describedby='" + this.parentName + "'] input[name='" + this.elementName + "']");
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + this.elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
        } else {
            this.driverExt.waitUntilElementVisibleByCssLocator("input[name='" + this.elementName + "']");
            return this.driverExt.findElement(By.cssSelector("input[name='" + this.elementName + "']"), Optional.of(3));
        }        
    }    
}
