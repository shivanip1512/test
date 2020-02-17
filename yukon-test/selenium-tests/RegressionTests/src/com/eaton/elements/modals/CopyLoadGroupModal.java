package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.TextEditElement;

public class CopyLoadGroupModal extends BaseModal {
    
    private WebDriver driver; 
    
    public CopyLoadGroupModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driver, "name", getModal());
    }
}
