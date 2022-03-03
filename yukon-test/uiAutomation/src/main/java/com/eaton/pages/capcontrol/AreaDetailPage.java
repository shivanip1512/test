package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AreaDetailPage extends PageBase {

    public AreaDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.CapControl.AREA_DETAIL + id;
    }
    
    public AreaDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}