package com.eaton.pages.assets.virtualdevices;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.WebTableColumnHeader;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.RecentArchievedRadingsModal;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
import com.eaton.elements.panels.VirtualDeviceInfoPanel;
import com.eaton.elements.panels.VirtualDevicePointsPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class VirtualDevicesDetailPage extends PageBase {
    private ActionBtnDropDownElement actionBtn;
    
    public VirtualDevicesDetailPage(DriverExtensions driverExt, String pageUrl, int virtualDeviceID) {
        super(driverExt);

        requiresLogin = true;
        this.pageUrl = pageUrl + virtualDeviceID;

        this.actionBtn = new ActionBtnDropDownElement(this.driverExt);
    }

    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }

    public VirtualDeviceInfoPanel getVirtualDeviceInfoPanel() {
        return new VirtualDeviceInfoPanel(this.driverExt, "Virtual Device Information");
    }

    public VirtualDevicePointsPanel getVirtualDevicePointsPanel() {
        return new VirtualDevicePointsPanel(this.driverExt, "Device Points");
    }

    public WebTableRow getPointsTableRow(int index) {
        return getVirtualDevicePointsPanel().getTable().getDataRowByIndex(index);
    }

    public WebTableColumnHeader getPointsPointsTableHeader() {
        return (WebTableColumnHeader) getVirtualDevicePointsPanel().getTable().getListTableHeaders();
    }

    public CreateVirtualDeviceModal showAndWaitCreateVirtualDeviceModal() {
        actionBtn.clickAndSelectOptionByText("Create");

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-virtual-device-popup");

        return new CreateVirtualDeviceModal(this.driverExt, Optional.empty(), Optional.of("js-create-virtual-device-popup"));
    }

    public EditVirtualDeviceModal showAndWaitEditVirtualDeviceModal() {
        getVirtualDeviceInfoPanel().getEdit().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-edit-virtual-device-popup");
        
        return new EditVirtualDeviceModal(this.driverExt, Optional.empty(), Optional.of("js-edit-virtual-device-popup"));
    }

    public ConfirmModal showAndWaitDeleteVirtualDeviceModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");

        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }

    public RecentArchievedRadingsModal showAndWaitRecentArchievedReadingsModal(String modalTitle, int index) {
        getPointsTableRow(1).getCellByIndex(index).click();

        SeleniumTestSetup.waitUntilModalOpenByTitle(modalTitle);

        return new RecentArchievedRadingsModal(this.driverExt, Optional.of(modalTitle), Optional.empty());
    }
}
