package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;

public class SearchBoxElement {
    private DriverExtensions driverExt;
    private String childElementName;
    private String parentElement;

    public SearchBoxElement(DriverExtensions driverExt, String elementName, String parentElement) {
        this.driverExt = driverExt;
        this.childElementName = elementName;
        this.parentElement = parentElement;        
    }  
    
    public SearchBoxElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.childElementName = elementName;
    }
    
    private WebElement getSearchBoxElement() {
        if (this.parentElement != null) {
            return this.driverExt.findElement(By.cssSelector("." + parentElement + " input[name='" + childElementName + "']"), Optional.empty());    
        } else {
            return this.driverExt.findElement(By.cssSelector(" input[name='" + childElementName + "']"), Optional.empty());
        }         
    }
    
    public void setSearchValue(String searchValue) {
        WebElement search =  getSearchBoxElement();
        
        search.click();
        search.clear();
        
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(search, searchValue).build().perform();
    }
    
    public void setSearchValueWithEnter(String searchValue) {
        WebElement search =  getSearchBoxElement();
        
        search.click();
        search.clear();
        
        Actions action = new Actions(driverExt.getDriver());
        action.sendKeys(search, searchValue).build().perform();
        action.sendKeys(Keys.ENTER).build().perform();
    }    

    public List<String> getSearchResults() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".ui-menu .ui-menu-item"), Optional.empty());
        
        List<String> results = new ArrayList<String>();
        for (WebElement webElement : list) {
            results.add(webElement.findElement(By.cssSelector("ui-menu-item-wrapper")).getText());
        }
        
        return results;
    }    
    
    public List<String> validateSearchTextPresentInSugestionList(String search) {
    	List<String> list=getSearchResults();
        List<String> matchingElements = list.stream()
          .filter(str -> str.trim().contains(search))
          .collect(Collectors.toList());
     
        return matchingElements;
    }
    
    public void setSearchValueAndClickResult(String searchValue) {        
        setSearchValue(searchValue);
        
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".ui-menu .ui-menu-item"), Optional.empty());
        
        WebElement value = list.stream().filter(element -> element.getText().contains(searchValue)).findFirst().orElseThrow();
        
        value.click();
    }
}