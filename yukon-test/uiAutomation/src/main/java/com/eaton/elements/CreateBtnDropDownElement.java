package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateBtnDropDownElement {

    private DriverExtensions driverExt;
    private WebElement parentElement;

    public CreateBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public CreateBtnDropDownElement(DriverExtensions driverExt, WebElement parentElement) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;
    }
    
    public WebElement getCreateBtn() {
        if (parentElement != null) {
        	return this.parentElement.findElement(By.cssSelector(".dropdown-trigger button"));
        } else {
	    	List<WebElement> buttons = this.driverExt.findElements(By.cssSelector(".page-actions .dropdown-trigger button"), Optional.of(3));
	
	    	return buttons.stream().filter(button -> button.findElement(By.cssSelector(".b-label")).getText().contains("Create")).findFirst().orElseThrow();
        }
    }

    public void click() {
        getCreateBtn().click();
    }

    public Boolean isDisplayed() {
        return getCreateBtn().isDisplayed();
    }

    public Boolean isEnabled() {
        return getCreateBtn().isEnabled();
    }

    public void clickAndSelectOptionByText(String value) {
        click();

        WebElement el = SeleniumTestSetup.getDriverExt().getDriverWait()
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dropdown-menu[style*='display: block;']")));

        List<WebElement> options = el.findElements(By.cssSelector(".dropdown-option"));

        WebElement optionValue = options.stream().filter(option -> option.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(value)).findFirst().orElseThrow();

        optionValue.click();

//        }  //TODO add an exception stating did not find dropdown
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
