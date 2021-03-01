package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class Button {

    private DriverExtensions driverExt;
    private String elementName;
    private String parrentClass;
    private WebElement parentElement;

    public Button(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public Button(DriverExtensions driverExt, String elementName, String parrentClass) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parrentClass = parrentClass;
    }

    public Button(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public WebElement getButton() {
        if (this.parrentClass != null) {
            return this.driverExt.findElement(By.cssSelector("." + this.parrentClass + " [aria-label='" + this.elementName + "']"), Optional.of(5));
        } else if (this.parentElement != null) {
            return parentElement.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"), Optional.of(5));
        }
    }

    public void click() {
        WebElement button = getButton();
        SeleniumTestSetup.moveToElement(button);
        getButton().click();
    }
}
