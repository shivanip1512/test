package com.eaton.pages.capcontrol.pointimport;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class PointImportPage extends PageBase {

    public PointImportPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.POINT_IMPORT;
    }
}
