package com.eaton.pages.capcontrol;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CapControlDashboardPage extends PageBase {

    private static final String DEFAULT_URL = Urls.CapControl.DASHBOARD;
    private ActionBtnDropDownElement actionBtn;

    public CapControlDashboardPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
