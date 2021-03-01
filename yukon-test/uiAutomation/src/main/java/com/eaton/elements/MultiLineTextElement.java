package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;

public class MultiLineTextElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;

    public MultiLineTextElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;        
    }   
    
    public MultiLineTextElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }  
    
    public MultiLineTextElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }  

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(3));

        return !list.isEmpty() ? true : false;
    }
    
    public void clearInputValue() {
        getMultiLineTextElement().clear();
    }

    //TODO this field can take in text with enters need to eventually change this input
    public void setInputValue(String value) {
        WebElement input = getMultiLineTextElement();
        
        input.clear();
        
        Actions action = new Actions(this.driverExt.getDriver());
        action.sendKeys(input, value).build().perform();
    }
    
    private WebElement getMultiLineTextElement() {
        if (this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] textarea[name='" + this.elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("textarea[name='" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("textarea[name='" + this.elementName + "']"), Optional.of(3));
        } 
    }
}
