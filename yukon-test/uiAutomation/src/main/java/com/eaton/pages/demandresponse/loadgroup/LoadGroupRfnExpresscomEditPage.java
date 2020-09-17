package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupRfnExpresscomCreatePage;

public class LoadGroupRfnExpresscomEditPage extends LoadGroupRfnExpresscomCreatePage {
    
    private DropDownElement controlPriority;

    public LoadGroupRfnExpresscomEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        requiresLogin = true;

        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
        
        controlPriority = new DropDownElement(this.driverExt, "protocolPriority");
    }
    
    public DropDownElement getControlPriority() {
        return controlPriority;
    }
}