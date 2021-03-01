package com.eaton.elements.modals.commchannel;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateCommChannelModal extends BaseModal {
    
    private TextEditElement name;
    private DropDownElement type;
    private DropDownElement baudRate;   

    protected static final String ARIADESCRIBEDBY = "js-create-comm-channel-popup";
    
    public CreateCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        name = new TextEditElement(this.driverExt, "name", ARIADESCRIBEDBY);
        type = new DropDownElement(this.driverExt, "type", ARIADESCRIBEDBY);
        baudRate = new DropDownElement(this.driverExt, "baudRate", ARIADESCRIBEDBY);
    }

    public TextEditElement getName() {
    	return name;
    }        
    
    public DropDownElement getType() {
    	return type;
    }    
    
    public DropDownElement getBaudRate() {
    	return baudRate;
    }    
        
    public void commChannelClickCancelAndWait() {
        List<WebElement> list = getModal().findElements(By.cssSelector(".ui-dialog-buttonset button"));

        list.stream().filter(x -> x.getText().contains("Cancel")).findFirst().orElseThrow().click();
        
        SeleniumTestSetup.waitUntilModalClosedDisplayNone(ARIADESCRIBEDBY);
    }
}
