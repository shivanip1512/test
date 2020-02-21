package com.eaton.pages.demandresponse;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class LoadGroupDetailPage extends PageBase {

    public LoadGroupDetailPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt, baseUrl);

    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");   
        
        return new ConfirmModal(this.driverExt, "yukon_dialog_confirm");        
    }
    
    public CopyLoadGroupModal showCopyLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        return new CopyLoadGroupModal(this.driverExt, "copy-loadGroup-popup");        
    }
}