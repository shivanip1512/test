package com.eaton.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextEditElement extends EditElement {
    
    public TextEditElement(WebDriver driver, String elementName) {
        super(driver, elementName);
    }
    
    public TextEditElement(WebDriver driver, String elementName, WebElement parentElement) {
        super(driver, elementName, parentElement);
    }
    
    public TextEditElement(WebDriver driver, String elementName, String parentName) {
        super(driver, elementName, parentName);
    }
    
    public void clearInputValue() {
        getEditElement().clear();
    }

    public void setInputValue(String value) {
        WebElement input = getEditElement();
        
        input.clear();
        input.sendKeys(value);
    }
}
