package com.eaton.elements;

import org.openqa.selenium.WebDriver;

public class SwitchButton extends Button {

    WebDriver driver;
    
    public SwitchButton(WebDriver driver, String elementName) {
        super(driver, elementName);
        
        this.driver = driver;
    }

    
}
