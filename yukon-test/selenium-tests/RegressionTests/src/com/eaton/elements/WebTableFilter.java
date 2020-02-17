package com.eaton.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebTableFilter {
    
    private WebElement filter;
    private TextEditElement name;
    private Button filterBtn;

    public WebTableFilter(WebElement filter, WebDriver driver) {
        
        this.filter = filter;
        name = new TextEditElement(driver, "name");
        filterBtn = new Button(driver, "Filter", this.filter);
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
