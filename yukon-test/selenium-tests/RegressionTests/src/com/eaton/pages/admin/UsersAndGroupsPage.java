package com.eaton.pages.admin;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UsersAndGroupsPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;

    public UsersAndGroupsPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
    }
}
