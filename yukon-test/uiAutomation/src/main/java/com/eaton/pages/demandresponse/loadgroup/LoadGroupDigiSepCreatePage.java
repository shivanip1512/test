package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupDigiSepCreatePage extends LoadGroupCreatePage {
    
    private DropDownMultiSelectElement deviceClass;
    private TextEditElement utilityEnrollmentGroup;
    private TextEditElement rampInTime;
    private TextEditElement rampOutTime;

    public LoadGroupDigiSepCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        deviceClass = new DropDownMultiSelectElement(this.driverExt, "deviceClassSet");
        utilityEnrollmentGroup = new TextEditElement(this.driverExt, "utilityEnrollmentGroup");
        rampInTime = new TextEditElement(this.driverExt, "rampInMinutes");
        rampOutTime =  new TextEditElement(this.driverExt, "rampOutMinutes");       
    }                  

    //Device Class Section
    public DropDownMultiSelectElement getDeviceClass() {
        return deviceClass;
    }

    //Enrollment Section
    public TextEditElement getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    // Timing Section
    public TextEditElement getRampInTime() {
        return rampInTime;
    }

    public TextEditElement getRampOutTime() {
        return rampOutTime;
    }
}
