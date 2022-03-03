package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class SearchBoxElement {
    private DriverExtensions driverExt;
    private String childElementName;
    private String parentElement;

    public SearchBoxElement(DriverExtensions driverExt, String parentElement, String childElementName) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;    
        this.childElementName = childElementName;          
    }  
    
    public SearchBoxElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.childElementName = elementName;
    }
    
    private WebElement getSearchBoxElement() {
    	if (this.parentElement != null) {
            return this.driverExt.findElement(By.cssSelector("." + this.parentElement + " input[name='" + this.childElementName + "']"), Optional.of(3));    
        } else {
            return this.driverExt.findElement(By.cssSelector(" input[name='" + this.childElementName + "']"), Optional.of(3));
        }         
    }
    
    public void setSearchValue(String searchValue) {
        WebElement search =  getSearchBoxElement();
        
        search.click();
        search.clear();
        
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(search, searchValue).build().perform();
    }
    
    public void setSearchValueAndEnter(String searchValue) {
        WebElement search =  getSearchBoxElement();
        
        search.click();
        search.clear();
        
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(search, searchValue).build().perform();
        action.sendKeys(Keys.ENTER).build().perform();
    }    

    public List<String> getSearchResults() {
        List<WebElement> list = SeleniumTestSetup.getDriverExt().getDriverWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".ui-menu .ui-menu-item")));
        
        List<String> results = new ArrayList<>();
        for (WebElement webElement : list) {
            results.add(webElement.findElement(By.cssSelector(".ui-menu-item-wrapper")).getText());
        }
        
        return results;
    }        
    
    public void setSearchValueAndClickResult(String searchValue) {        
        setSearchValue(searchValue);
        
        List<WebElement> list = SeleniumTestSetup.getDriverExt().getDriverWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".ui-menu .ui-menu-item .ui-menu-item-wrapper")));
        
        WebElement value = list.stream().filter(element -> element.getText().contains(searchValue)).findFirst().orElseThrow();
        
        value.click();
    }
}