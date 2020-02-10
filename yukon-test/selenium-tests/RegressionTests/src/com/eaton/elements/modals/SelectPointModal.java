package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.WebTable;

public class SelectPointModal extends BaseModal {
    
    public WebTable table;
    public WebDriver driver;

    public SelectPointModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
        table = new WebTable(this.driver, "compact-results-table");
    }
}
