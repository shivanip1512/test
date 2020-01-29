package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Button {

    private WebDriver driver;
    private String elementName;

    public Button(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;        
    }
    
    protected WebElement getButton() {
        return this.driver.findElement(By.cssSelector("[aria-label='" + this.elementName + "']"));
    }

    public void click() {
        getButton().click();
    }
}
