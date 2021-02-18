package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;

public class EditCommChannelModal extends BaseModal {
    
    private TabElement tabs;

    public EditCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        tabs = new TabElement(this.driverExt, getModal()); 
    }
    
    public TabElement getTabs() {
        return tabs;             
    }
    
    public WebElement getInfoTab() {
        return getTabs().getTabPanelByName("Info");
    }
    
    public WebElement getConfigTab() {
        return getTabs().getTabPanelByName("Configuration");
    }    
    
    public Section getTimingSection() {
        return new Section(this.driverExt, "Timing", getConfigTab());
    }
        
    //Info Tab
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
    
    public SwitchBtnYesNoElement getStatus() {
        return new SwitchBtnYesNoElement(this.driverExt, "enable", getModal());
    }
        
    //Configuration Tab - Timing Section    
    public TextEditElement getPreTxWait() {
        return new TextEditElement(this.driverExt, "timing.preTxWait", getModal());
    }
           
    public TextEditElement getRtsToTxWait() {
        return new TextEditElement(this.driverExt, "timing.rtsToTxWait", getModal());
    }
        
    public TextEditElement getPostTxWait() {
        return new TextEditElement(this.driverExt, "timing.postTxWait", getModal());
    }
        
    public TextEditElement getReceiveDataWait() {
        return new TextEditElement(this.driverExt, "timing.receiveDataWait", getModal());
    }
    
    public TextEditElement getAdditionalTimeOut() {
        return new TextEditElement(this.driverExt, "timing.extraTimeOut", getModal());
    }       
}
