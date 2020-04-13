package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class FeederDetailPage extends PageBase {
    

    public FeederDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.FEEDER_DETAIL + id;
    }
    
    public FeederDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}