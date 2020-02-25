package com.eaton.pages.demandresponse;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class ScenarioDetailPage extends PageBase {

    public ScenarioDetailPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt, baseUrl);

    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteControlAreaModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");   
        
        return new ConfirmModal(this.driverExt, "yukon_dialog_confirm");        
    }    
}