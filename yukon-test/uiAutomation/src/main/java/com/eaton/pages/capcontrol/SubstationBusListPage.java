package com.eaton.pages.capcontrol;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationBusListPage extends PageBase {

    private WebTable table;

    public SubstationBusListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_BUS_LIST;

        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
