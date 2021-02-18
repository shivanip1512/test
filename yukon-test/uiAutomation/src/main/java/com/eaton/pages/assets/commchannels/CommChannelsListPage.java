package com.eaton.pages.assets.commchannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;
import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.commchannel.CreateLocalSerialPortCommChannelModal;
import com.eaton.elements.modals.commchannel.CreateTcpCommChannelModal;
import com.eaton.elements.modals.commchannel.CreateTerminalServerCommChannelModal;
import com.eaton.elements.modals.commchannel.CreateUdpCommChannelModal;

public class CommChannelsListPage extends PageBase {
    
    private ActionBtnDropDownElement actionBtn;
    
    public CommChannelsListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNELS_LIST;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }

    public WebTable getTable() {
        return new WebTable(driverExt, "compact-results-table");
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }    
    
    public CreateUdpCommChannelModal showAndWaitCreateUdpCommChannelModal() {        
    	actionBtn.clickAndSelectOptionByText("Create");
    	
    	SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-comm-channel-popup");
    	
    	return new CreateUdpCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }   
    
    public CreateTcpCommChannelModal showAndWaitCreateTcpCommChannelModal() {        
        actionBtn.clickAndSelectOptionByText("Create");
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-comm-channel-popup");
        
        return new CreateTcpCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }  
    
    public CreateTerminalServerCommChannelModal showAndWaitCreateTerminalServerCommChannelModal() {        
        actionBtn.clickAndSelectOptionByText("Create");
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-comm-channel-popup");
        
        return new CreateTerminalServerCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }  
    
    public CreateLocalSerialPortCommChannelModal showAndWaitCreateLocalSerialPortCommChannelModal() {        
        actionBtn.clickAndSelectOptionByText("Create");
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-comm-channel-popup");
        
        return new CreateLocalSerialPortCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }  
}