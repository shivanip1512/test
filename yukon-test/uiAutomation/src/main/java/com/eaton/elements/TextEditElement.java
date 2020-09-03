package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

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
        WebElement input = getEditElement();
        SeleniumTestSetup.scrollToElement(input);
        input.clear();
    }

    public void setInputValue(String value) {
        WebElement input = getEditElement();
        SeleniumTestSetup.scrollToElement(input);

        input.click();
        input.clear();

        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(input, value).build().perform();
    }

    public String getInputValue() {
        return getEditElement().getAttribute("value");
    }

    public boolean isDisabled() {
        WebElement input = getEditElement();

        return input.isEnabled();
    }

    public String getValidationError() {
        String by = "span[id='" + this.elementName + ".errors']";
        
        return this.driverExt.findElement(By.cssSelector(by), Optional.of(3)).getText();        
    }
    
    public String getMaxLength() {
        return getEditElement().getAttribute("maxLength");
    }
}
