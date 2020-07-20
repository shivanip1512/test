package com.eaton.pages.admin;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SearchPage extends PageBase {

    public SearchPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.SEARCH;
    }
}
