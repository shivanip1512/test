package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class EditElement {

	public static final String ELEMENT_TYPE_INPUT = "input";
	public static final String ELEMENT_TYPE_TEXTAREA = "textarea";
	
    protected DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private String elementType;

    public EditElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.elementType = ELEMENT_TYPE_INPUT;
    }
    
    public EditElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        this.elementType = ELEMENT_TYPE_INPUT;
    }  
    
    public EditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        this.elementType = ELEMENT_TYPE_INPUT;
    }
    
    public EditElement(DriverExtensions driverExt, String elementName, WebElement parentElement, String elementType) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        this.elementType = elementType;
    }

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(5));

        return !list.isEmpty() ? true : false;
    }

    public WebElement getEditElement() {
        if (this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] " + elementType + "[name='" + this.elementName + "']"), Optional.of(5));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector(elementType + "[name='" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector(elementType + "[name='" + this.elementName + "']"), Optional.of(5));
        }        
    }    
}
