package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class RadioButtonElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;

    public RadioButtonElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public RadioButtonElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }

    public RadioButtonElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }
    
    public void selectByValue(String value) {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".radio-btn .b-label"), Optional.of(0));
        
        WebElement el = list.stream().filter(x -> x.getText().contains(value)).findFirst().orElseThrow();
        
        el.click();
    }

    public List<String> getValues() {
        List<WebElement> elements = getRadioButtons();

        List<String> values = new ArrayList<>();
        for (WebElement element : elements) {
            String value = element.getAttribute("value");

            values.add(value);
        }

        return values;
    }

    private List<WebElement> getRadioButtons() {
        if (this.parentName != null) {
            return this.driverExt.findElements(By.cssSelector("[aria-describedby='" + this.parentName + "'] .radio-btn input[name = '" + this.elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElements(By.cssSelector(".radio-btn input[name = '" + this.elementName + "']"));
        } else {
            return this.driverExt.findElements(By.cssSelector(".radio-btn input[name = '" + this.elementName + "']"), Optional.of(3));
        }
    }

    public String getValueChecked() {
        List<WebElement> elements = getRadioButtons();
        
        for (WebElement el : elements) {            
            if(el.getAttribute("checked") != null) {
                return el.getAttribute("value");
            } 
        }
        
        return null;
    }

    public void moveTo() {
        List<WebElement> list = getRadioButtons();
        WebElement el = list.get(0);

        SeleniumTestSetup.moveToElement(el);
    }
}
