package com.eaton.pages.tools.trends;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class TrendsDetailPage extends TrendsListPage {

    public TrendsDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS_DETAIL + id;
    }
}