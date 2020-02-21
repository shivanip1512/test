package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class BaseModal {

    private DriverExtensions driverExt;
    private String modalName;
    private WebElement modal;

    public BaseModal(DriverExtensions driverExt, String modalName) {
        this.driverExt = driverExt;
        this.modalName = modalName;
        
        this.modal = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.modalName + "']"), Optional.empty());
    }

    // TODO need a unique way to select the new user, new user group and new role group the describedby changes
    protected WebElement getModal() {
        return this.modal;
    }
    
    public String getModalTitle() {
        return getModal().findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickClose() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
        
        SeleniumTestSetup.waitUntilModalClosed(modalName);
    }

    // TODO need a unique way to select the save button
    public void clickOk() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
        
        SeleniumTestSetup.waitUntilModalClosed(modalName);
    }

    ///TODO need a unique way to select the cancel button
    public void clickCancel() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
        
        SeleniumTestSetup.waitUntilModalClosed(modalName);
    }  
}
