package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Button {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;

    public Button(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public Button(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }

    public Button(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public WebElement getButton() {
        if (this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] [aria-label='" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            return parentElement.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"), Optional.empty()); 
        }
    }
    
    public void click() {
        getButton().click();
    } 
}
