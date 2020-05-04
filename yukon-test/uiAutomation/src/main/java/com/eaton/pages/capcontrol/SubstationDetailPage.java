package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationDetailPage extends PageBase {
    

    public SubstationDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_DETAIL + id;
    }
    
    public SubstationDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}