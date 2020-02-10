package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiLineTextElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;

    public MultiLineTextElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
    }   

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driver.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"));

        return !list.isEmpty() ? true : false;
    }
    
    public void clearInputValue() {
        getmultiLineTextElement().clear();
    }

    //TODO this field can take in text with enters need to eventually change this input
    public void setInputValue(String value) {
        WebElement input = getmultiLineTextElement();
        
        input.clear();
        input.sendKeys(value);
    }

    protected WebElement getmultiLineTextElement() {
        if (this.parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] textarea[name='" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("textarea[name='" + this.elementName + "']"));
        }        
    }
}
