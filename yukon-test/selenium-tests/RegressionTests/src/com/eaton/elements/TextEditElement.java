package com.eaton.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextEditElement extends EditElement {
    
    private WebDriver driver;
    private String elementName;

    public TextEditElement(WebDriver driver, String elementName, String parent) {
        super(driver, elementName, parent);

        this.driver = driver;
        this.elementName = elementName;
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
