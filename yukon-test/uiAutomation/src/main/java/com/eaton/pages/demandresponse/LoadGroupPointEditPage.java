package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupPointEditPage extends LoadGroupPointCreatePage {

    public LoadGroupPointEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }
}
