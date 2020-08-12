package com.eaton.pages.tools.trends;

import com.eaton.framework.DriverExtensions;

public class TrendCreatePage extends TrendPage {

    public TrendCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        requiresLogin = true;
        this.pageUrl = pageUrl;
    }
}
