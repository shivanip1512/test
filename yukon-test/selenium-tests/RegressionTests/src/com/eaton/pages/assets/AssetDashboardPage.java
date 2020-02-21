package com.eaton.pages.assets;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AssetDashboardPage extends PageBase {

    public static final String DEFAULT_URL = Urls.SUPPORT;

    public AssetDashboardPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
    }
}
