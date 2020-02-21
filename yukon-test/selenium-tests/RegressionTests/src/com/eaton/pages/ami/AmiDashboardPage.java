package com.eaton.pages.ami;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class AmiDashboardPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public AmiDashboardPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
    
    public CreateMeterModal showCreateMeterModal() {
        
        actionBtn.clickAndSelectOptionByText("Create Meter");        
                      
        SeleniumTestSetup.waitUntilModalVisible("contentPopup");
        
        return new CreateMeterModal(this.driverExt, "contentPopup");        
    }    
}
