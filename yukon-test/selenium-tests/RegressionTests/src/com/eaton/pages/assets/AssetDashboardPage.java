package com.eaton.pages.assets;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AssetDashboardPage extends PageBase {

    public AssetDashboardPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.DASHBOARD;
    }
}
