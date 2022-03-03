package com.eaton.pages.capcontrol;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CbcListPage extends PageBase {

    private WebTable table;

    public CbcListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CBC_LIST;

        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
