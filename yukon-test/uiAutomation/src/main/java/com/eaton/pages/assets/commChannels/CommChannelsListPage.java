package com.eaton.pages.assets.commChannels;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;
import com.eaton.elements.Button;
import com.eaton.elements.Link;
import com.eaton.elements.WebTable;

public class CommChannelsListPage extends PageBase {
    
    private WebTable table;
    
    public CommChannelsListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.Comm_Channels_List;
        setTable(new WebTable(driverExt, "compact-results-table"));
    }

    public WebTable getTable() {
        return this.table;
    }

    private void setTable(WebTable table) {
        this.table = table;
    }
    
    public Boolean getLinkVisibility(String channelName) {
        Link link = new Link(driverExt, channelName);
        return link.isDisplayed();
    }
}
