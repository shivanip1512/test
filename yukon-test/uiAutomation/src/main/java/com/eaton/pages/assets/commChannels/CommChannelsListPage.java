package com.eaton.pages.assets.commChannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;
import com.eaton.elements.WebTable;

public class CommChannelsListPage extends PageBase {
    
    private WebTable table;
    
    public CommChannelsListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNELS_LIST;
        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
}
