package com.eaton.pages.demandresponse;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class ControlAreaListPage extends PageBase {

    private WebTable table;

    public ControlAreaListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CONTROL_AREA;

        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
