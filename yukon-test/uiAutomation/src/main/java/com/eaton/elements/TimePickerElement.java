package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.eaton.framework.DriverExtensions;

public class TimePickerElement {

    private DriverExtensions driverExt;
    private String elementName;

    public TimePickerElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public void setValue(String value) {
        WebElement picker = getPicker(elementName);

        picker.clear();
        picker.sendKeys(value);
    }

    public void clearValue() {
        getPicker(elementName).clear();
    }

    public WebElement getPicker(String elementName) {
        return this.driverExt.findElement(By.cssSelector("input[name='" + elementName + "']"), Optional.empty());
    }

}