package com.eaton.pages.assets.virtualdevices;

import java.util.Optional;
import com.eaton.elements.Button;
import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.elements.WebTableColumnHeader;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.RecentArchievedRadingsModal;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
import com.eaton.elements.panels.Panels;
import com.eaton.elements.panels.VirtualDeviceInfoPanel;
import com.eaton.elements.panels.VirtualDevicePointsPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class VirtualDevicesDetailPage extends VirtualDevicesListPage {
    int virtualDeviceID;
    private Panels panels;
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
        panels = new Panels(driverExt);

        this.editButton = new Button(this.driverExt, "Edit");
        this.filterButton = new Button(this.driverExt, "Filter");
        this.pointTypeDropdown = new DropDownMultiSelectElement(this.driverExt, "pointTypeSelector");
        this.deviceInfoPanel = new VirtualDeviceInfoPanel(this.driverExt, getPanelList().getListOfPanelNames().get(0));
        this.devicePointPanel = new VirtualDevicePointsPanel(this.driverExt, getPanelList().getListOfPanelNames().get(1));
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
	
	public WebTableRow getPointsTableRow(int index) {
		WebTableRow row = getVirtualDevicePointsPanel().getTable().getDataRowByIndex(index);
		return row;
	}
	
	public WebTableColumnHeader getPointsPointsTableHeader() {
		WebTableColumnHeader headerRow = (WebTableColumnHeader) getVirtualDevicePointsPanel().getTable().getColumnHeaders();
		return headerRow;
		
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
	
	public Panels getPanelList() {
    	return panels;
    }
}
