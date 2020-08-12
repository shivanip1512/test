package com.eaton.pages.tools.trends;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class TrendsDetailPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public TrendsDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS_DETAIL + id;
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }

    public TrendsDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }

    public WebTable getTable() {
        return new WebTable(driverExt, "compact-results-table");
    }

    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}