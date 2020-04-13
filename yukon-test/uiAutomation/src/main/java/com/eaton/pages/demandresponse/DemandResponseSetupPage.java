package com.eaton.pages.demandresponse;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class DemandResponseSetupPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public DemandResponseSetupPage(DriverExtensions driverExt) {
        super(driverExt);

        this.requiresLogin = true;
        pageUrl = Urls.DemandResponse.SETUP;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public DemandResponseSetupPage(DriverExtensions driverExt, String filter) {
        super(driverExt);

        this.requiresLogin = true;
        pageUrl = Urls.DemandResponse.SETUP_FILTER + filter;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
