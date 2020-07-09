package com.eaton.pages.assets.commchannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;
import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;

public class CommChannelsListPage extends PageBase {
    
    private WebTable table;
    private ActionBtnDropDownElement actionBtn;
    
    public CommChannelsListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNELS_LIST;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }    
    
    public ConfirmModal showAndWaitCreateCommChannelModal() {        
        actionBtn.clickAndSelectOptionByText("Create");        
                      
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-create-comm-channel-popup");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }      
}