package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WebTableSearch {
    
    private TextEditElement search;  
    WebDriver driver;

    public WebTableSearch(WebDriver driver) {
        
        this.driver = driver;
        search = new TextEditElement(this.driver, "ss", null);        
    } 
    
    public void enterSearchCritera(String criteria) {
        search.setInputValue(criteria);
        clickClearBtn();
    }
    
    public void clearSearch() {
        search.clearInputValue();
        clickClearBtn();
    }  
    
    public void clickClearBtn() {
        this.driver.findElement(By.id("picker-voltReduction-show-all-link")).click();
    }
}
