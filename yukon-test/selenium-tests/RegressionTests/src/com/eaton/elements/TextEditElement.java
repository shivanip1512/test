package com.eaton.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;

public class TextEditElement extends EditElement {
    
    private DriverExtensions driverExt;
    
    public TextEditElement(DriverExtensions driverExt, String elementName) {
        super(driverExt, elementName); 
        this.driverExt = driverExt;
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
        
        Actions action = new Actions(this.driverExt.getDriver());
        action.sendKeys(input, value).build().perform();
    }
}
