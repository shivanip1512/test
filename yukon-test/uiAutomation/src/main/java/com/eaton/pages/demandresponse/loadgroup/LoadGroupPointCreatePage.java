package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class LoadGroupPointCreatePage extends LoadGroupCreatePage {
    
    private PickerElement controlDevicePoint;

    public LoadGroupPointCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        controlDevicePoint = new PickerElement(this.driverExt, Optional.of("pointGroupControlDevicePicker"), Optional.empty());
    }

    public PickerElement getControlDevicePoint() {
        return controlDevicePoint;
    }

    public String getControlDevicePointLabelText() {
        return getControlDevicePoint().getLinkValue();
    }
    
    public String getControlDevicePointValidationMsg() {
        return getControlDevicePoint().getValidationError("deviceUsage.id");
    }

    public DropDownElement getControlStartState() {
        return new DropDownElement(this.driverExt, "startControlRawState.rawState");
    }

    public SelectPointModal showAndWaitPointGroupControlDeviceModal() {

        getControlDevicePoint().clickLink();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("pointGroupControlDevicePicker");

        return new SelectPointModal(this.driverExt, Optional.empty(), Optional.of("pointGroupControlDevicePicker"));
    }
}
