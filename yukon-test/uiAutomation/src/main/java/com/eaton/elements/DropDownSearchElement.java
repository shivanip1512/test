package com.eaton.elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class DropDownSearchElement {

    private DriverExtensions driverExt;
    private String elementId;
    private String parentName;
    private WebElement parentElement;
    private WebElement searchDropDown;

    public DropDownSearchElement(DriverExtensions driverExt, String elementId) {
        this.driverExt = driverExt;
        this.elementId = elementId;
        
        setDropDownSearchElement();
    }    
    
    public DropDownSearchElement(DriverExtensions driverExt, String elementId, String parentName) {
        this.driverExt = driverExt;
        this.elementId = elementId;
        this.parentName = parentName;
        
        setDropDownSearchElement();
    } 
    
    public DropDownSearchElement(DriverExtensions driverExt, String elementId, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementId = elementId;
        this.parentElement = parentElement;
        
        setDropDownSearchElement();
    } 
    
    public void selectItemByTextSearch(String searchText) {
        WebElement dropDown = getDropDownSearchElement();
        
        dropDown.click();
        
        WebElement search = dropDown.findElement(By.cssSelector(".chosen-search input"));
        
        search.sendKeys(searchText);
        
        WebElement results = dropDown.findElement(By.cssSelector(".chosen-results"));
        
        List<WebElement> filteredResults = results.findElements(By.cssSelector(".active-result"));
        
        for (WebElement result : filteredResults) {
            String text = result.getText();
            if (text.equals(searchText)) {
                result.click();
                return;
            }
        }
    }
    
    private void setDropDownSearchElement() {
        if (this.parentName != null) {
            this.searchDropDown = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] #" + this.elementId ), Optional.of(3));
        } else if (this.parentElement != null) {
            this.searchDropDown = this.parentElement.findElement(By.id(this.elementId));
        } else {
            this.searchDropDown = this.driverExt.findElement(By.id(this.elementId), Optional.of(3));   
        }        
    }    
    
    private WebElement getDropDownSearchElement() {
        return this.searchDropDown;
    }
    
    public List<String> getDropDownItems() {
        WebElement dropDown = getDropDownSearchElement();
        
        dropDown.click();
        
        WebElement results = dropDown.findElement(By.cssSelector(".chosen-results"));
        
        List<WebElement> filteredResults = results.findElements(By.cssSelector(".active-result"));
        List<String> dropDownItems = new LinkedList<String>();
        for (WebElement result : filteredResults) {
        	dropDownItems.add(result.getText());
        }
        
        return dropDownItems;
    }
}
