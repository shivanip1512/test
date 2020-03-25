package com.eaton.pages.support;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SupportPage extends PageBase {

    public static final String DEFAULT_URL = Urls.SUPPORT;

    public SupportPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
    }
}
