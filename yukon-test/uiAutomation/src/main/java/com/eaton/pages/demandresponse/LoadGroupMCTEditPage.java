package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;

public class LoadGroupMCTEditPage extends LoadGroupMCTCreatePage {

	public LoadGroupMCTEditPage(DriverExtensions driverExt) {
		super(driverExt);

		requiresLogin = true;

	}

}
