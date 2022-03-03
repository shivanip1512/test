package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class WebTableSearch {
    
    private TextEditElement search;  
    DriverExtensions driverExt;

    public WebTableSearch(DriverExtensions driverExt) {
        
        this.driverExt = driverExt;
        search = new TextEditElement(this.driverExt, "ss");        
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
        this.driverExt.findElement(By.id("picker-voltReduction-show-all-link"), Optional.of(3)).click();
    }
}
