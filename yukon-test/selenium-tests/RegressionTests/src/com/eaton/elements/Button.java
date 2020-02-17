package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Button {

    private WebDriver driver;
    private String elementName;
    private WebElement btn;
    private String parentName;
    private WebElement parentElement;

    public Button(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
        
        setButton();
    }

    public Button(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;

        setButton();
    }

    public Button(WebDriver driver, String elementName, WebElement parentElement) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;

        setButton();
    }

    public WebElement getButton() {
        return btn;
    }

    private void setButton() {
        if (this.parentName != null) {
            btn = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] [aria-label='" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            btn = parentElement.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));
        } else {
            btn = this.driver.findElement(By.cssSelector("[aria-label='" + this.elementName + "']")); 
        }
    }    

    public void click() {
        getButton().click();
    }
}
