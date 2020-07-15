package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
//import com.eaton.elements.Label;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.tabs.TabElement;
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
    public TextEditElement getChannelPreTxWait() {
        return new TextEditElement(this.driverExt, "timing.preTxWait", getModal());
    }
    public Section getTimingSection() {
        return new Section(this.driverExt, "Timing", getModal());
    }
    
    public TextEditElement getChannelRTSTxWait() {
        return new TextEditElement(this.driverExt, "timing.rtsToTxWait", getModal());
    }
    
    public TextEditElement getChannelPostTxWait() {
        return new TextEditElement(this.driverExt, "timing.postTxWait", getModal());
    }
    
    public TextEditElement getChannelRecDataWait() {
        return new TextEditElement(this.driverExt, "timing.receiveDataWait", getModal());
    }
    
    public TextEditElement getChannelAdditionalTimeOut() {
        return new TextEditElement(this.driverExt, "timing.extraTimeOut", getModal());
    }
    
    public TextEditElement getIPAddress() {
        return new TextEditElement(this.driverExt, "meterNumber", getModal());
    }

    public TextEditElement getPortNumber() {
        return new TextEditElement(this.driverExt, "address", getModal());
    }

    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate", getModal());
    }

    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled", getModal());
    }
    
    public TabElement getTabElement() {
        return new TabElement(this.driverExt, getModal());
    }
        
}
