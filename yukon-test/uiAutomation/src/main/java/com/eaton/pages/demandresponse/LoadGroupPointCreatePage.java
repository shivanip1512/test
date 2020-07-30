package com.eaton.pages.demandresponse;

import java.util.Optional;

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

    public SelectPointModal showAndWaitPointGroupControlDeviceModal(String modalTitle) {

        getControlDevicePoint().clickLink();

        SeleniumTestSetup.waitUntilModalVisibleByTitle("Select Control Device");

        return new SelectPointModal(this.driverExt, Optional.of(modalTitle), Optional.of("pointGroupControlDevicePicker"));
    }
}
