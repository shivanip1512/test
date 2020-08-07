package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class ActionBtnDropDownElement {

    private DriverExtensions driverExt;

    public ActionBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;    
    }
    
    public WebElement getActionBtn() {
        return this.driverExt.findElement(By.cssSelector(".page-actions #b-page-actions button"), Optional.empty());
    }

    public void click() {
        getActionBtn().click();
    }

    public Boolean isDisplayed() {
        return getActionBtn().isDisplayed();
    }
    
    public Boolean isEnabled() {
        return getActionBtn().isEnabled();
    }
    
    public void clickAndSelectOptionByText(String value) {
        click();                
        
        WebElement element = null;
        //long startTime = System.currentTimeMillis();
        //while (element == null && System.currentTimeMillis() - startTime < 3000) {
            element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));
        //}
        
        if (element != null) {
            
            List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option-label"));
            
            for (WebElement option : options) {
                String optionText = option.getText();
                if (optionText.equals(value)) {
                    option.click();
                    return;
                }
            }
        }  //TODO add an exception stating did not find dropdown
    }
}
