package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupItronEditPage extends LoadGroupCreatePage {
    
    private DropDownElement relay;

    public LoadGroupItronEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        relay = new DropDownElement(this.driverExt, "virtualRelayId");
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;                
    }
    
    public DropDownElement getRelay() {
        return relay;
    }
}
