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
    private WebElement button;
    private WebElement btnDetails;

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
        setButton();
    }

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName, String parrentClass) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
        this.parrentClass = parrentClass;
        setButton();
    }

    public IconLinkButton(DriverExtensions driverExt, String linkCategoryName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.linkCategoryName = linkCategoryName.toUpperCase();
        this.parentElement = parentElement;
        setButton();
    }

    private void setButton() {
        if (this.parrentClass != null) {
            button = this.driverExt.findElement(By.cssSelector("." + this.parrentClass + "button[data-href='edit?category=" + this.linkCategoryName + "']"), Optional.of(2));
            btnDetails = this.driverExt.findElement(By.cssSelector("." + this.parrentClass + "button[data-href='edit?category=" + this.linkCategoryName + "'] + .box"), Optional.of(2));
        } else if (this.parentElement != null) {
            button = parentElement.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "']"));
            btnDetails = parentElement.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "'] + .box"));
        } else {
            button = this.driverExt.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "']"), Optional.of(2));
            btnDetails = this.driverExt.findElement(By.cssSelector("button[data-href='edit?category=" + this.linkCategoryName + "'] + .box"), Optional.of(2));
        }
    }
    
    private WebElement getButton() {
        return button;
    }

    public void click() {
        getButton().click();
    }
    
    public String getTitle() {
        WebElement el = btnDetails.findElement(By.cssSelector(".title"));
        return el.getText();
    }
    
    public String getDetails() {
        WebElement el = btnDetails.findElement(By.cssSelector(".detail"));
        
        return el.getText();
    }
    
    public String getButtonLink() {
        return getButton().getAttribute("data-href");
    }
    
    public String getTitleLink() {
        WebElement el = btnDetails.findElement(By.cssSelector(".title"));
        return el.getAttribute("href");
    }
}
