package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupItronCreatePage extends LoadGroupCreatePage {
    
    private DropDownElement relay;
    
    public LoadGroupItronCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        relay = new DropDownElement(this.driverExt, "virtualRelayId");
    } 
    
    public DropDownElement getRelay() {
        return relay;
    }

}
