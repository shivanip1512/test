package com.eaton.pages.assets.virtualdevices;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Button;
import com.eaton.elements.CreateBtnDropDownElement;
import com.eaton.elements.DropDownMultiSelectElement;
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
    int virtualDeviceID;
    private ActionBtnDropDownElement actionBtn;
    private Button editButton;
    private Button filterButton;
    private DropDownMultiSelectElement pointTypeDropdown;
    private VirtualDeviceInfoPanel deviceInfoPanel;
    private VirtualDevicePointsPanel devicePointPanel;
    
    public VirtualDevicesDetailPage(DriverExtensions driverExt, String pageUrl, int virtualDeviceID) {
        super(driverExt);
        
        requiresLogin = true;
        
        this.virtualDeviceID = virtualDeviceID;
        this.pageUrl = pageUrl + virtualDeviceID;;

        this.actionBtn = new ActionBtnDropDownElement(this.driverExt);
        this.editButton = new Button(this.driverExt, "Edit");
        this.filterButton = new Button(this.driverExt, "Filter");
        this.pointTypeDropdown = new DropDownMultiSelectElement(this.driverExt, "pointTypeSelector");
        
        this.deviceInfoPanel = new VirtualDeviceInfoPanel(this.driverExt, "Virtual Device Information");
        this.devicePointPanel = new VirtualDevicePointsPanel(this.driverExt, "Device Points");
    }

    public ActionBtnDropDownElement getActionBtn() {
		return actionBtn;
	}
    
    public Button getEdit() {
        return editButton;
    }

	public VirtualDeviceInfoPanel getVirtualDeviceInfoPanel() {
		return deviceInfoPanel;
	}
	
	public VirtualDevicePointsPanel getVirtualDevicePointsPanel() {
		return devicePointPanel;
	}
	
	public DropDownMultiSelectElement getPointType() {
		return pointTypeDropdown;
	}
	
	public Button getFilter() {
		return filterButton;
	}
	
	public CreateBtnDropDownElement getCreateBtn() {
	    return new CreateBtnDropDownElement(this.driverExt, getVirtualDevicePointsPanel().getPanel());
	}
	
	public WebTableRow getPointsTableRow(int index) {
		WebTableRow row = getVirtualDevicePointsPanel().getTable().getDataRowByIndex(index);
		return row;
	}
	
	public WebTableColumnHeader getPointsPointsTableHeader() {
		WebTableColumnHeader headerRow = (WebTableColumnHeader) getVirtualDevicePointsPanel().getTable().getColumnHeaders();
		return headerRow;
		
	}
	
	public CreateVirtualDeviceModal showAndWaitCreateVirtualDeviceModal() {
		actionBtn.clickAndSelectOptionByText("Create");

		SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-create-virtual-device-popup");

		return new CreateVirtualDeviceModal(this.driverExt, Optional.empty(),
				Optional.of("js-create-virtual-device-popup"));
	}
	
	public EditVirtualDeviceModal showAndWaitEditVirtualDeviceModal() {
        getEdit().click();
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-edit-virtual-device-popup");
        return new EditVirtualDeviceModal(this.driverExt, Optional.empty(),
                Optional.of("js-edit-virtual-device-popup"));
    }
	
    public ConfirmModal showAndWaitDeleteVirtualDeviceModal() {
		getActionBtn().clickAndSelectOptionByText("Delete");
		
		SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");

        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
	}
	
	public RecentArchievedRadingsModal showAndWaitRecentArchievedReadingsModal(String modalTitle, int index) {                
		getPointsTableRow(1).getCell(index).click();
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(modalTitle);
        
        return new RecentArchievedRadingsModal(this.driverExt, Optional.of(modalTitle) ,Optional.empty());
    }
	
}
