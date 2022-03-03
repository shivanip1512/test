package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupVersacomEditPage extends LoadGroupVersacomCreatePage {

    public LoadGroupVersacomEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }

}
