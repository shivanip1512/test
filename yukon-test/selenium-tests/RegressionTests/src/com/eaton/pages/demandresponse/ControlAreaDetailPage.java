package com.eaton.pages.demandresponse;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class ControlAreaDetailPage extends PageBase {

    public ControlAreaDetailPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt, baseUrl);

    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteControlAreaModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");   
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));    
    }    
}