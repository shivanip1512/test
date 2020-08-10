package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class ButtonByClass {

    private DriverExtensions driverExt;
    private String className;
    private String parentName;
    private WebElement parentElement;

    public ButtonByClass(DriverExtensions driverExt, String className) {
        this.driverExt = driverExt;
        this.className = className;
    }

    public ButtonByClass(DriverExtensions driverExt, String className, String parentName) {
        this.driverExt = driverExt;
        this.className = className;
        this.parentName = parentName;
    }

    public ButtonByClass(DriverExtensions driverExt, String className, WebElement parentElement) {
        this.driverExt = driverExt;
        this.className = className;
        this.parentElement = parentElement;
    }

    public WebElement getButtonByClass() {
        if (this.parentName != null) {
            return this.driverExt.findElement(
                    By.cssSelector("[aria-describedby='" + this.parentName + "'] [." + this.className + "]"),
                    Optional.empty());
        } else if (this.parentElement != null) {
            return parentElement.findElement(By.cssSelector("." + this.className));
        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.className), Optional.empty());
        }
    }

    public void click() {
        getButtonByClass().click();
    }
}
