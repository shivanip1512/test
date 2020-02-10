package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Button {

    private WebDriver driver;
    private String elementName;
    private String parentName; 

    public Button(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    protected WebElement getButton() {
        if (parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] [aria-label='" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));   
        }                
    }

    public void click() {
        getButton().click();
    }
}
