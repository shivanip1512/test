package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ActionBtnDropDownElement {

    private WebDriver driver;

    public ActionBtnDropDownElement(WebDriver driver) {
        this.driver = driver;    
    }
    
    public WebElement getActionBtn() {
        return this.driver.findElement(By.cssSelector(".page-actions #b-page-actions button"));
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
        long startTime = System.currentTimeMillis();
        while (element == null && System.currentTimeMillis() - startTime < 3000) {
            element = this.driver.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"));
        }
        
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
