package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class DropDownMultiSelectElement {
    private DriverExtensions driverExt;
    private String parentName;
    private String elementName;
    private WebElement parentElement;

    public DropDownMultiSelectElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }
    
    public DropDownMultiSelectElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public DropDownMultiSelectElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }
    
    /**
     * Select the text in Dropdown with input having class 'chosen-search-input'
     * 
     * @param searchText text to select in Dropdown
     *                 
     */
    public void selectItemByText(String searchText) {
        WebElement dropDown = getElement();
        
        dropDown.click();
        
        WebElement search = dropDown.findElement(By.cssSelector(".chosen-search-input"));
        
        search.sendKeys(searchText);
        
        WebElement results = dropDown.findElement(By.cssSelector(".chosen-drop"));
        
        List<WebElement> filteredResults = results.findElements(By.cssSelector(".chosen-results .active-result"));
        
        for (WebElement result : filteredResults) {
            String text = result.getText();
            if (text.equals(searchText)) {
                result.click();
                return;
            }
        }
    }
    
    private WebElement getElement() {
        if (this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] .chosen-container"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector(".chosen-container"));
        } else {
            return this.driverExt.findElement(By.cssSelector(".chosen-container"), Optional.of(3));   
        }        
    } 

    
    public String getValidationError() {
        return this.driverExt.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(3)).getText();
    }
    
    
    /**
     * Clears the text in Dropdown with input having class 'chosen-search-input'
     */
    public void clearSelectedItem() {
    	WebElement dropDown = getElement();
        
        dropDown.click();
        
        WebElement search = dropDown.findElement(By.cssSelector(".chosen-search-input"));
        
        search.sendKeys("\u0008");
    }
}
