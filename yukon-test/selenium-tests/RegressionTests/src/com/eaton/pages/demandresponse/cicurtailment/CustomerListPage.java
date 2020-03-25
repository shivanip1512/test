package com.eaton.pages.demandresponse.cicurtailment;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CustomerListPage extends PageBase {

    public CustomerListPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CI_CUSTOMER_LIST;
    }
}
