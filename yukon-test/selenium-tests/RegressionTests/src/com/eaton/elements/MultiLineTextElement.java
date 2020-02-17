package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiLineTextElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement multiEditElement;

    public MultiLineTextElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
        
        setMultiLineTextElement();
    }   
    
    public MultiLineTextElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setMultiLineTextElement();
    }  
    
    public MultiLineTextElement(WebDriver driver, String elementName, WebElement parentElement) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setMultiLineTextElement();
    }  

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driver.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"));

        return !list.isEmpty() ? true : false;
    }
    
    public void clearInputValue() {
        getMultiLineTextElement().clear();
    }

    //TODO this field can take in text with enters need to eventually change this input
    public void setInputValue(String value) {
        WebElement input = getMultiLineTextElement();
        
        input.clear();
        input.sendKeys(value);
    }

    private WebElement setMultiLineTextElement() {
        if (this.parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] textarea[name='" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("textarea[name='" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("textarea[name='" + this.elementName + "']"));
        }        
    }
    
    private WebElement getMultiLineTextElement() {
        return this.multiEditElement;
    }
}
