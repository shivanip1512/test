package com.eaton.elements.modals;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class ConfirmModal extends BaseModal {
    
    public ConfirmModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
    }
    
    public String getConfirmMsg() {
        return getModal().findElement(By.cssSelector("#yukon_dialog_confirm .confirm-message")).getText();
    }
}
