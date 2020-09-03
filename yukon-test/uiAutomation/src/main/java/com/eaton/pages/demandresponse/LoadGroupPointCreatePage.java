package com.eaton.pages.demandresponse;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class LoadGroupPointCreatePage extends LoadGroupCreatePage {

    public LoadGroupPointCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    public PickerElement getControlDevicePoint() {
        return new PickerElement(this.driverExt, "picker-pointGroupControlDevicePicker-btn");
    }

    public String getControlDevicePointLabelText() {
        return getControlDevicePoint().getLinkValue();
    }

    public DropDownElement getControlStartState() {
        return new DropDownElement(this.driverExt, "startControlRawState.rawState");
    }

    public SelectPointModal showAndWaitPointGroupControlDeviceModal() {

        getControlDevicePoint().clickLink();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("pointGroupControlDevicePicker");

        return new SelectPointModal(this.driverExt, Optional.empty(), Optional.of("pointGroupControlDevicePicker"));
    }
}
