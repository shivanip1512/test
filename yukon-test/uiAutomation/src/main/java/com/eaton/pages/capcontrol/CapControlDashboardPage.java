package com.eaton.pages.capcontrol;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CapControlDashboardPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public CapControlDashboardPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.CapControl.DASHBOARD;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
