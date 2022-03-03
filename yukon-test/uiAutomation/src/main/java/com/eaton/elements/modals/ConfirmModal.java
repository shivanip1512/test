package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class ConfirmModal extends BaseModal {

    public ConfirmModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

    }

    public String getConfirmMsg() {
        return getModal().findElement(By.cssSelector("#yukon_dialog_confirm .confirm-message")).getText();
    }
    
    public String getWarningMsg() {
        return getModal().findElement(By.cssSelector("#yukon_dialog_confirm .user-message")).getText();
    }
}
