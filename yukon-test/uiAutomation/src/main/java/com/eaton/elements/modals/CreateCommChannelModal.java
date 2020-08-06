package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateCommChannelModal extends BaseModal {

    private String modalName; 
    
    private static final String modalAriaDescribedBy = "js-create-comm-channel-popup";
    
    public CreateCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }

    public TextEditElement getName() {
    	return new TextEditElement(this.driverExt, "name", modalAriaDescribedBy);
    }        
    
    public DropDownElement getType() {
    	return new DropDownElement(this.driverExt, "type", modalAriaDescribedBy);
    }
    
    public TextEditElement getIpAddress() {
        return new TextEditElement(this.driverExt, "ipAddress", modalAriaDescribedBy);
    }
    
    public TextEditElement getPortNumber() {
    	return new TextEditElement(this.driverExt, "portNumber", modalAriaDescribedBy);
    } 
    
    public DropDownElement getBaudRate() {
    	return new DropDownElement(this.driverExt, "baudRate", modalAriaDescribedBy);
    }
    
    public DropDownElement getPhysicalPort() {
    	return new DropDownElement(this.driverExt, "physicalPort", modalAriaDescribedBy);
    }
    
    public TextEditElement getPhysicalPortOther() {
    	return new TextEditElement(this.driverExt, "physicalPort", modalAriaDescribedBy);
    }    

    public void clickClose() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalName);
    }

    // TODO need a unique way to select the save button
    public void clickSave() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalName);
    }

    ///TODO need a unique way to select the cancel button
    public void clickCancel() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalName);
    }       
}
