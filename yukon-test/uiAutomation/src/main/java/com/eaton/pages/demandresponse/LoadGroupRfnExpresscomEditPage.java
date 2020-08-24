package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;

public class LoadGroupRfnExpresscomEditPage extends LoadGroupRfnExpresscomCreatePage {
	 
	public LoadGroupRfnExpresscomEditPage(DriverExtensions driverExt) {
	        super(driverExt);
	        requiresLogin = true;
	    }

}
