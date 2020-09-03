package com.eaton.pages.demandresponse;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class ScenarioDetailPage extends PageBase {

    public ScenarioDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.SCENARIO_DETAILS + id;

    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteControlAreaModal() {
        getActionBtn().clickAndSelectOptionByText("Delete"); 
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));      
    }    
}