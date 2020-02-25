package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Button {

    private DriverExtensions driverExt;
    private String elementName;
    private WebElement btn;
    private String parentName;
    private WebElement parentElement;

    public Button(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        
        setButton();
    }

    public Button(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;

        setButton();
    }

    public Button(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;

        setButton();
    }

    public WebElement getButton() {
        return btn;
    }
    
    public void click() {
        getButton().click();
    }

    private void setButton() {
        if (this.parentName != null) {
            btn = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] [aria-label='" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            btn = parentElement.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));
        } else {
            btn = this.driverExt.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"), Optional.empty()); 
        }
    }    
}
