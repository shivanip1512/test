package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.Button;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class MarkerModal extends BaseModal {
    
    private String modalTitle;
    private String describedBy;

    public MarkerModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        if(modalTitle.isPresent()) {
            this.modalTitle = modalTitle.get();
        }
        
        if(describedBy.isPresent()) {
            this.describedBy = describedBy.get();
        }
    } 
    
    public TextEditElement getValue() {
        return new TextEditElement(this.driverExt, "multiplier", getModal());
    }
    
    public TextEditElement getLabel() {
        return new TextEditElement(this.driverExt, "label", getModal());
    }
    
    public RadioButtonElement getAxis() {
        return new RadioButtonElement(this.driverExt, "axis", getModal());
    }
    
    public void clickClose() {
        getModal().findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalTitle);
    }

    // TODO need a unique way to select the save button
    public void clickSave() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalTitle);
    }

    ///TODO need a unique way to select the cancel button
    public void clickCancel() {
        getModal().findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalTitle);
    }  
}
