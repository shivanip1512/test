package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CapBankDetailPage extends PageBase {

    public CapBankDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl =   Urls.CapControl.CAP_BANK_DETAIL + id;
    }
    
    public CapBankDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}