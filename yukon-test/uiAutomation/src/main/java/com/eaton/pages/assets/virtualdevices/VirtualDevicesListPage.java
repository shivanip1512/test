package com.eaton.pages.assets.virtualdevices;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class VirtualDevicesListPage extends PageBase {
	private ActionBtnDropDownElement actionBtn;

	public VirtualDevicesListPage(DriverExtensions driverExt) {
		super(driverExt);

		requiresLogin = true;
		pageUrl = Urls.Assets.VIRTUAL_DEVICES;
		actionBtn = new ActionBtnDropDownElement(this.driverExt);
	}

	public WebTable getTable() {
		return new WebTable(driverExt, "compact-results-table");
	}

	public ActionBtnDropDownElement getActionBtn() {
		return actionBtn;
	}

	public CreateVirtualDeviceModal showAndWaitCreateVirtualDeviceModal() {
		actionBtn.clickAndSelectOptionByText("Create");

		SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-virtual-device-popup");

		return new CreateVirtualDeviceModal(this.driverExt, Optional.empty(),
				Optional.of("js-create-virtual-device-popup"));
	}
}
