package com.eaton.pages.demandresponse;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
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
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CopyLoadProgramModal showCopyLoadProgramModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        return new CopyLoadProgramModal(this.driverExt, Optional.of("Copy Load Program"), Optional.empty()); 
    }
}