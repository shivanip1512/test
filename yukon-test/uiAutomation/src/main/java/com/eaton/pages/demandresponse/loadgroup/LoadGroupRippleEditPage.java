package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupRippleEditPage extends LoadGroupRippleCreatePage {

    public LoadGroupRippleEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }
}
