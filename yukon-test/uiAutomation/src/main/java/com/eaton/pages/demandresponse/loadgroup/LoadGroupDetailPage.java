package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupDetailPage extends PageBase {

    public LoadGroupDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_DETAIL + id;
    }
    
    public LoadGroupDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Delete"); 
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CopyLoadGroupModal showCopyLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        return new CopyLoadGroupModal(this.driverExt, Optional.of("Copy Load Group"), Optional.empty()); 
    }
    
    public WebTable getTable() {
        return new WebTable(this.driverExt, "noswitchtype");
    }
}