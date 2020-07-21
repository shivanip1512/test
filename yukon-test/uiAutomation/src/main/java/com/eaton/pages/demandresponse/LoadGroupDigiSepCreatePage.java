package com.eaton.pages.demandresponse;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupDigiSepCreatePage extends LoadGroupCreatePage {

    public LoadGroupDigiSepCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    public DropDownElement getDeviceClass() {
        return new DropDownElement(this.driverExt, "deviceClassSet");
    }

    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driverExt, "utilityEnrollmentGroup");
    }

    // Timing
    public TextEditElement getRampInTime() {
        return new TextEditElement(this.driverExt, "rampInMinutes");
    }

    public TextEditElement getRampOutTime() {
        return new TextEditElement(this.driverExt, "rampOutMinutes");
    }
}
