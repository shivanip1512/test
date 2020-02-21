package com.eaton.pages.demandresponse;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class DemandResponseDashboardPage extends PageBase {

    public static final String DEFAULT_URL = Urls.DemandResponse.DASHBOARD;
    private ActionBtnDropDownElement actionBtn;

    public DemandResponseDashboardPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }

    public String getQuickSearchesUrl(String linkText) {
        return this.driverExt.findElement(By.linkText(linkText), Optional.empty()).getAttribute("href");
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
