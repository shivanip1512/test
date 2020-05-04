package com.eaton.elements;

import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class WebTableFilter {
    
    private WebElement filter;
    private TextEditElement name;
    private Button filterBtn;

    public WebTableFilter(WebElement filter, DriverExtensions driverExt) {
        
        this.filter = filter;
        name = new TextEditElement(driverExt, "name");
        filterBtn = new Button(driverExt, "Filter", this.filter);
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
