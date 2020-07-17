package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.framework.DriverExtensions;

public class SearchBoxElement {
    private DriverExtensions driverExt;
    private WebDriver driver;
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
            return this.driverExt.findElement(By.cssSelector("." + this.parentElement + " input[name='" + this.childElementName + "']"), Optional.empty());    
        } else {
            return this.driverExt.findElement(By.cssSelector(" input[name='" + this.childElementName + "']"), Optional.empty());
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
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".ui-menu .ui-menu-item"), Optional.empty());
        
        List<String> results = new ArrayList<>();
        for (WebElement webElement : list) {
            results.add(webElement.findElement(By.cssSelector(".ui-menu-item-wrapper")).getText());
        }
        
        return results;
    }        
    
    public void setSearchValueAndClickResult(String searchValue) {        
        setSearchValue(searchValue);
        
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".ui-menu .ui-menu-item"), Optional.empty());
        
        WebElement value = list.stream().filter(element -> element.getText().contains(searchValue)).findFirst().orElseThrow();
        
        value.click();
    }
}