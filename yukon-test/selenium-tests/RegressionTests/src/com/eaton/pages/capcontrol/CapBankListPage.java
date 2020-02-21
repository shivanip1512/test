package com.eaton.pages.capcontrol;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class CapBankListPage extends PageBase {

    private WebTable table;

    public CapBankListPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt, baseUrl);

        setTable(new WebTable(this.driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
