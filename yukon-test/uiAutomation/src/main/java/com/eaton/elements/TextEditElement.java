package com.eaton.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
        
        input.click();
        input.clear();
        
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(input, value).build().perform();
    }
}
