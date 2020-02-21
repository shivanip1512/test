package com.eaton.elements;

import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class TextEditElement extends EditElement {
    
    public TextEditElement(DriverExtensions driverExt, String elementName) {
        super(driverExt, elementName);
    }
    
    public TextEditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        super(driverExt, elementName, parentElement);
    }
    
    public TextEditElement(DriverExtensions driverExt, String elementName, String parentName) {
        super(driverExt, elementName, parentName);
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
