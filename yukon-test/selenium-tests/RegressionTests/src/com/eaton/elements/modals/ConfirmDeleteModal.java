package com.eaton.elements.modals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmDeleteModal extends BaseModal {
    
    public ConfirmDeleteModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
    }
    
    public String getConfirmDeleteMsg() {
        return getModal().findElement(By.cssSelector("#yukon_dialog_confirm .confirm-message")).getText();
    }
}
