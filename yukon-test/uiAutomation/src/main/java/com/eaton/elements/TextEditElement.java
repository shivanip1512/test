package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;

public class TextEditElement extends EditElement {
   
    private String elementName;
    
    public TextEditElement(DriverExtensions driverExt, String elementName) {
        super(driverExt, elementName); 
        this.elementName = elementName;
    }
    
    public TextEditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        super(driverExt, elementName, parentElement);
        this.elementName = elementName;
    }
    
    public TextEditElement(DriverExtensions driverExt, String elementName, String parentName) {
        super(driverExt, elementName, parentName);
        this.elementName = elementName;
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
    
    public String getValidationError() {
       return this.driverExt.findElement(By.cssSelector(this.elementName + ".errors"), Optional.empty()).getText();
    }
}
