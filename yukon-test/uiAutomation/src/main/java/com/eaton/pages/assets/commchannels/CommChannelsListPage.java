package com.eaton.pages.assets.commchannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;
import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.CreateCommChannelModal;

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
    
    public CreateCommChannelModal showAndWaitCreateCommChannelModal() {        
    	actionBtn.clickAndSelectOptionByText("Create");
    	
    	SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-create-comm-channel-popup");
    	
    	return new CreateCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }      
}