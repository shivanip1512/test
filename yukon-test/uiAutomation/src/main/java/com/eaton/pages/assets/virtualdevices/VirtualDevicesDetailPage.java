package com.eaton.pages.assets.virtualdevices;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class VirtualDevicesDetailPage extends PageBase {
    int virtualDeviceID;

    public VirtualDevicesDetailPage(DriverExtensions driverExt, int virtualDeviceID) {
        super(driverExt);
        requiresLogin = true;
        this.virtualDeviceID = virtualDeviceID;
        pageUrl = Urls.Assets.VIRTUAL_DEVICES_EDIT + "/" + virtualDeviceID;

    }

    public Button getEditButton() {
        return new Button(this.driverExt, "Edit");
    }

    public EditVirtualDeviceModal showAndWaitEditVirtualDeviceModal() {
        getEditButton().click();
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-edit-virtual-device-popup");
        return new EditVirtualDeviceModal(this.driverExt, Optional.empty(),
                Optional.of("js-edit-virtual-device-popup"));
    }
}
