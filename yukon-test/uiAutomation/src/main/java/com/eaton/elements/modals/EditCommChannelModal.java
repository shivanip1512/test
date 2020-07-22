package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;

public class EditCommChannelModal extends BaseModal {

    public EditCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
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
    public Section getSharingSection() {
        return new Section(this.driverExt, "Sharing", getModal());
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
        return new TextEditElement(this.driverExt, "portNumber", getModal());
    }

    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate", getModal());
    }

    public DropDownElement getPhysicalPort() {
    	return new DropDownElement(this.driverExt, "physicalPort", getModal());
    }
    
    public TextEditElement getPhysicalPortOther() {
    	return new TextEditElement(this.driverExt, "physicalPort", getModal());
    } 
    
    public SwitchBtnYesNoElement getStatus() {
        return new SwitchBtnYesNoElement(this.driverExt, "disable", getModal());
    }
    
    public TabElement getTabElement() {
        return new TabElement(this.driverExt, getModal());
    }
       
    public RadioButtonElement getProtocolWrap() {
    	return new RadioButtonElement(this.driverExt, "protocolWrap", getModal());
    } 
    public TextEditElement getCarrierDetectWait() {
        return new TextEditElement(this.driverExt, "carrierDetectWaitInMilliseconds", getModal());
    }
    public TextEditElement getEncryptionKey() {
        return new TextEditElement(this.driverExt, "keyInHex", getModal());
    }
    public SwitchBtnYesNoElement getCheckedButtonCarrier() {
        return new SwitchBtnYesNoElement(this.driverExt, "carrierDetectWait", getModal());
    }
    public TextEditElement getSocketNumber() {	
        return new TextEditElement(this.driverExt, "sharing.sharedSocketNumber", getModal());	
    }	
    public RadioButtonElement getSharedPortType() {	
    	return new RadioButtonElement(this.driverExt, "sharing.sharedPortType", getModal());
    } 	
	

}
