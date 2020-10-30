package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class IconLinkButton {
    
    private DriverExtensions driverExt;
    private String linkCategoryName;
    private String parrentClass;
    private WebElement parentElement;

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
    }

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName, String parrentClass) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
        this.parrentClass = parrentClass;
    }

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
        this.parentElement = parentElement;
    }

    private WebElement getButton() {
        if (this.parrentClass != null) {
            return this.driverExt.findElement(By.cssSelector("." + this.parrentClass + "button[data-href='edit?category=" + this.linkCategoryName + "']"), Optional.of(2));
        } else if (this.parentElement != null) {
            return parentElement.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "']"), Optional.of(2));
        }
    }

    public void click() {
        getButton().click();
    }
    
    public String getTitle() {
        WebElement el = getButton().findElement(By.cssSelector("+ .box .title"));
        return el.getText();
    }
    
    public String getDetails() {
        WebElement el = getButton().findElement(By.cssSelector("+ .box .detail"));
        return el.getText();
    }
    
    public String getButtonLink() {
        return getButton().getAttribute("data-href");
    }
    
    public String getTitleLink() {
        WebElement el = getButton().findElement(By.cssSelector("+ .box .title"));
        return el.getAttribute("href");
    }
}
