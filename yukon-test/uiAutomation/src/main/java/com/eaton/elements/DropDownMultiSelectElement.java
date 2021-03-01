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
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] select[name='" + elementName + "'] + .chosen-container"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("select[name='" + elementName + "'] + .chosen-container"));
        } else {
            return this.driverExt.findElement(By.cssSelector("select[name='" + elementName + "'] + .chosen-container"), Optional.of(3));
        }        
    } 

    
    public String getValidationError() {
        return this.driverExt.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(3)).getText();
    }
    
    
    /**
     * @param index - zero based, removes selected value based on index
     */
    public void removeItemByIndex(int index) {
    	WebElement dropDown = getElement();
        
        List<WebElement> choices = dropDown.findElements(By.cssSelector(".chosen-choices .search-choice"));
        
        choices.get(index).findElement(By.cssSelector(".search-choice-close")).click();
    }
}
