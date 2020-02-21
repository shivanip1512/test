package com.eaton.pages.demandresponse;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class LoadProgramDetailPage extends PageBase {

    public LoadProgramDetailPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt, baseUrl);

    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteLoadProgramModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");   
        
        return new ConfirmModal(this.driverExt, "yukon_dialog_confirm");        
    }
    
    public CopyLoadGroupModal showCopyLoadProgramModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        return new CopyLoadGroupModal(this.driverExt, "copy-loadProgram-popup");        
    }
}