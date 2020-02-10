package com.eaton.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebTableFilter {
    
    private WebElement filter;
    public TextEditElement name;
    public Button filterBtn;

    public WebTableFilter(WebElement filter, WebDriver driver) {
        
        this.filter = filter;
        name = new TextEditElement(driver, "name", null);
        filterBtn = new Button(driver, "Filter", null);
    } 
    
    public void enterFilterCritera(String criteria) {
        name.setInputValue(criteria);
        filterBtn.click();
    }
    
    public void clearFilterCriteria() {
        name.clearInputValue();
        filterBtn.click();
    }       
}
