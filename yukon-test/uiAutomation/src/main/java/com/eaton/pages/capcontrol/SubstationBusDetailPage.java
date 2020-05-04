package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationBusDetailPage extends PageBase {
    

    public SubstationBusDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_BUS_DETAIL + id;
    }
    
    public SubstationBusDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}