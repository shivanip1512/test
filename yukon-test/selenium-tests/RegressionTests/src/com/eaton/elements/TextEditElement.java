package com.eaton.elements;

import org.openqa.selenium.WebDriver;

public class TextEditElement extends EditElement{
    
    private WebDriver driver;
    private String elementName;

    public TextEditElement(WebDriver driver, String elementName) {
        super(driver, elementName);

        this.driver = driver;
        this.elementName = elementName;
    }
    
    public void clearInputValue() {
        getEditElement().clear();
    }

    public void setInputValue(String value) {
        getEditElement().sendKeys(value);
    }

}
