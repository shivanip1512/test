package com.eaton.pages.ami;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AmiDashboardPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public AmiDashboardPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Ami.AMI_DASHBOARD;

        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
    
    public CreateMeterModal showAndWaitCreateMeterModal() {
        
        actionBtn.clickAndSelectOptionByText("Create Meter");        
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("contentPopup");
        
        return new CreateMeterModal(this.driverExt, Optional.empty(), Optional.of("contentPopup"));
    }    
}
