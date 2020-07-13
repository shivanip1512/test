package com.eaton.elements.modals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateCommChannelModal extends BaseModal{

    private DriverExtensions driverExt;
    private String modalName; 
    private WebElement modal;
    
    public CreateCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
    }

    private static final String PARENT_NAME = "js-create-comm-channel-popup";
    
    public TextEditElement getName() {
    	return new TextEditElement(this.driverExt, "name", PARENT_NAME);
    }        
    
    public DropDownElement getType() {
    	return new DropDownElement(this.driverExt, "type", PARENT_NAME);
    }
    
    public TextEditElement getPortNumber() {
    	return new TextEditElement(this.driverExt, "portNumber", PARENT_NAME);
    } 
    
    public DropDownElement getBaudRate() {
    	return new DropDownElement(this.driverExt, "baudRate", PARENT_NAME);
    }
    public String getModalTitle() {
        return modal.findElement(By.cssSelector(".ui-dialog-titlebar .ui-dialog-title")).getText();
    }

    public void clickClose() {
        modal.findElement(By.cssSelector(".ui-dialog-titlebar-close")).click();
        
        SeleniumTestSetup.waitUntilModalClosedByTitle(this.modalName);
    }

    // TODO need a unique way to select the save button
    public void clickSave() {
        modal.findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
    }

    ///TODO need a unique way to select the cancel button
    public void clickCancel() {
        modal.findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
    }
    
    public List<String> getTabLabels() {
    	List<WebElement> nameElements = getModal().findElements(By.cssSelector("table tr .name"));
		List<String> names = new ArrayList<String>();
        for (WebElement element : nameElements) {
        	names.add(element.getText());
        }
    	
        return names;	
    }
}
