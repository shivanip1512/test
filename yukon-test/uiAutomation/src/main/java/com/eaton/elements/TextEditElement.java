package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class TextEditElement extends EditElement {

    private String elementName;
    private WebElement parentElement;

    public TextEditElement(DriverExtensions driverExt, String elementName) {
        super(driverExt, elementName);
        this.elementName = elementName;
    }

    public TextEditElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        super(driverExt, elementName, parentElement);
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public TextEditElement(DriverExtensions driverExt, String elementName, String parentName) {
        super(driverExt, elementName, parentName);
        this.elementName = elementName;
    }

    public void clearInputValue() {
        WebElement input = getEditElement();
        SeleniumTestSetup.moveToElement(input);
        input.clear();
    }

    public void setInputValue(String value) {
        WebElement input = getEditElement();
        SeleniumTestSetup.moveToElement(input);

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
        String validationError = "";
        long startTime = System.currentTimeMillis();

        while (validationError.equals("") && (System.currentTimeMillis() - startTime) < 5000) {
            try {
                if (this.parentElement != null) {
                    validationError = this.parentElement.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']")).getText(); 
                    System.out.println("parent error: " + validationError);
                } else {
                    validationError = this.driverExt.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.empty()).getText();   
                    System.out.println("error: " + validationError);
                }
            } catch (StaleElementReferenceException | NoSuchElementException ex) {
            }
        }
        
        return validationError;
    }
    
    public String getMaxLength() {
        return getEditElement().getAttribute("maxLength");
    }
    
    public String getPlaceHolder() {
        return getEditElement().getAttribute("placeholder");
    }
}
