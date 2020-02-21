package com.eaton.pages.demandresponse;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class DemandResponseSetupPage extends PageBase {

    public static final String DEFAULT_URL = Urls.DemandResponse.SETUP;
    private ActionBtnDropDownElement actionBtn;

    public DemandResponseSetupPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
