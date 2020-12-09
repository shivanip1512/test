package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class TimePickerElement {

    private DriverExtensions driverExt;
    private String elementName;

    public TimePickerElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public void setValue(String value) {
        WebElement picker = getPicker(elementName);
        SeleniumTestSetup.moveToElement(picker);
        picker.clear();
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(picker, value).build().perform();
        
    }

    public void clearValue() {
        getPicker(elementName).clear();
    }

    public WebElement getPicker(String elementName) {
        return this.driverExt.findElement(By.cssSelector("input[name='" + elementName + "']"), Optional.empty());
    }
    
    public String getValidationError() {
        String validationError = "";
        long startTime = System.currentTimeMillis();

        while (validationError.equals("") && (System.currentTimeMillis() - startTime) < 5000) {
            try {
                if (this.elementName != null) {

                    validationError = this.driverExt.findElement(By.cssSelector("div[class='error']"), Optional.empty()).getText();   
                    System.out.println("error: " + validationError);
                }
            } catch (StaleElementReferenceException | NoSuchElementException ex) {
            }
        }
        
        return validationError;
    }

}