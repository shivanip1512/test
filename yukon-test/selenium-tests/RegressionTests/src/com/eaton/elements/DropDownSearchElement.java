package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DropDownSearchElement {

    private WebDriver driver;
    private String elementId;
    private String parentName;
    private WebElement parentElement;
    private WebElement searchDropDown;

    public DropDownSearchElement(WebDriver driver, String elementId) {
        this.driver = driver;
        this.elementId = elementId;
        
        setDropDownSearchElement();
    }    
    
    public DropDownSearchElement(WebDriver driver, String elementId, String parentName) {
        this.driver = driver;
        this.elementId = elementId;
        this.parentName = parentName;
        
        setDropDownSearchElement();
    } 
    
    public DropDownSearchElement(WebDriver driver, String elementId, WebElement parentElement) {
        this.driver = driver;
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
            this.searchDropDown = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] #" + this.elementId ));
        } else if (this.parentElement != null) {
            this.searchDropDown = this.parentElement.findElement(By.id(this.elementId));
        } else {
            this.searchDropDown = this.driver.findElement(By.id(this.elementId));   
        }        
    }    
    
    private WebElement getDropDownSearchElement() {
        return this.searchDropDown;
    }
}
