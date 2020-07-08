package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.DropDownElement;
//import com.eaton.elements.Label;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class EditCommChannelModal extends BaseModal {

    private DriverExtensions driverExt;

    public EditCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
    }

    // TODO need to add all fields as there are more based on the device you select

    public TextEditElement getChannelName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }

    public TextEditElement getIPAddress() {
        return new TextEditElement(this.driverExt, "meterNumber", getModal());
    }

    public TextEditElement getPortNumber() {
        return new TextEditElement(this.driverExt, "address", getModal());
    }

    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "routeId", getModal());
    }

    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled", getModal());
    }
    
    public String getUserMessage() {
    	return this.driverExt.findElements(By.cssSelector(".name-value-table.natural-width>tbody > tr:nth-child(1) > td:nth-child(2) > span"), Optional.empty()).get(2).getText();
    }
}
