package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class RegulatorDetailPage extends PageBase {
    

    public RegulatorDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.REGULATOR_DETAIL + id;
    }
    
    public RegulatorDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}