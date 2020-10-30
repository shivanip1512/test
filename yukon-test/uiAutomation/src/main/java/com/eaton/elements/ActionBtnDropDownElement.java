package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class ActionBtnDropDownElement {

    private DriverExtensions driverExt;

    public ActionBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public WebElement getActionBtn() {
        return this.driverExt.findElement(By.cssSelector(".page-actions #b-page-actions button"), Optional.of(0));
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

    public void clickAndSelectOptionByText(String text) {
        click();

        WebElement element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));

        List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option-label"));

        WebElement el = options.stream().filter(x -> x.getText().equals(text)).findFirst().orElseThrow();

        el.click();
    }

    /**
     * @param text - The Text listed in the dropdown
     * @return - returns true if the Action in the dropdown is enabled
     *           returns false if the Action in the dropdown is disabled
     */
    public Boolean isActionEnabled(String text) {
        WebElement element = null;
        element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));

        List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option-label"));

        WebElement el = options.stream().filter(x -> x.getText().equals(text)).findFirst().orElseThrow();

        return !el.getAttribute("class").contains("disabled");
    }
    
    /**
     * This method is used to get the link/href attribute from Action Dropdown
     * 
     * @param text - The text listed in dropdown
     * @return - returns href attribute of option in dropdown
     */
    public String getOptionLinkByText(String text) {
    	click();
    	
        List<WebElement> dropdownOptions;
        WebElement anchorElement = null;
        
        WebElement dropdownMenu = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), 
        		 					Optional.of(3));

        try {
        	dropdownOptions = dropdownMenu.findElements(By.cssSelector(".dropdown-menu[style*='display: block;'] .dropdown-option"));
        	 
        	List<WebElement> options = dropdownMenu.findElements(By.cssSelector(".dropdown-option-label"));
             
            WebElement el = options.stream().filter(x -> x.getText().equals(text)).findFirst().orElseThrow();
             
            if (!el.equals(null)) {
            	anchorElement = dropdownOptions.stream().filter(x -> x.getText().equals(text)).findFirst().orElseThrow();  
                anchorElement = anchorElement.findElement(By.cssSelector("a"));
             }                                  
         } 
         catch(StaleElementReferenceException ex) {
         } 
         
         return anchorElement.getAttribute("href");
    }
}
