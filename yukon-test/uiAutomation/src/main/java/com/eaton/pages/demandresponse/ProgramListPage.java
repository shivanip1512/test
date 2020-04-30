package com.eaton.pages.demandresponse;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class ProgramListPage extends PageBase {

    private WebTable table;

    public ProgramListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.PROGRAMS;

        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
