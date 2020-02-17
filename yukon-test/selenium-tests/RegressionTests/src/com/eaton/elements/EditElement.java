package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement inputElement;

    public EditElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;     
        
        setEditElement();
    }
    
    public EditElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setEditElement();
    }  
    
    public EditElement(WebDriver driver, String elementName, WebElement parentElement) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setEditElement();
    }

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driver.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"));

        return !list.isEmpty() ? true : false;
    }

    protected void setEditElement() {
        if (this.parentName != null) {
            this.inputElement = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            this.inputElement = this.parentElement.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
        } else {
            this.inputElement = this.driver.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
        }        
    }
    
    protected WebElement getEditElement() {
        return this.inputElement;
    }
}
