package com.eaton.pages.ami;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.elements.panels.DeviceConfigPanel;
import com.eaton.elements.panels.DeviceGroupsPanel;
import com.eaton.elements.panels.MeterEventsPanel;
import com.eaton.elements.panels.MeterInfoPanel;
import com.eaton.elements.panels.MeterReadingsPanel;
import com.eaton.elements.panels.MeterTrendPanel;
import com.eaton.elements.panels.NetworkInfoPanel;
import com.eaton.elements.panels.NotesPanel;
import com.eaton.elements.panels.OutagesPanel;
import com.eaton.elements.panels.Panels;
import com.eaton.elements.panels.TimeOfUsePanel;
import com.eaton.elements.panels.WiFiConnectionPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class WRL420cLMeterDetailsPage extends PageBase {
	
	private Panels panels;

    public WRL420cLMeterDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Ami.METER_DETAIL + id;
        panels = new Panels(driverExt);
    }
    
    public WRL420cLMeterDetailsPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getAction() {
        return new ActionBtnDropDownElement(this.driverExt);
    }

    public MeterInfoPanel getMeterInfoPanel() {
    	return new MeterInfoPanel(this.driverExt, getPanelList().getListOfPanelNames().get(0));
    }
    
    public MeterReadingsPanel getMeterReadingsPanel() {
        return new MeterReadingsPanel(this.driverExt, getPanelList().getListOfPanelNames().get(1));
    }
    
    public WiFiConnectionPanel getWiFiConnectionPanel() {
        return new WiFiConnectionPanel(this.driverExt, getPanelList().getListOfPanelNames().get(2));
    }
    
    public NetworkInfoPanel getNetworkInfoPanel() {
        return new NetworkInfoPanel(this.driverExt, getPanelList().getListOfPanelNames().get(3));
    }
    
    public NotesPanel getNotesPanel() {
        return new NotesPanel(this.driverExt, getPanelList().getListOfPanelNames().get(4));
    }
    
    public DeviceGroupsPanel getDeviceGroupsPanel() {
        return new DeviceGroupsPanel(this.driverExt, getPanelList().getListOfPanelNames().get(5));
    }
    
    public MeterTrendPanel getMeterTrendPanel() {
        return new MeterTrendPanel(this.driverExt, getPanelList().getListOfPanelNames().get(6));
    }
    
    public MeterEventsPanel getMeterEventsPanel() {
        return new MeterEventsPanel(this.driverExt, getPanelList().getListOfPanelNames().get(7));
    }
    
    public OutagesPanel getOutagesPanel() {
        return new OutagesPanel(this.driverExt, getPanelList().getListOfPanelNames().get(8));
    }
    
    public TimeOfUsePanel getTimeOfUsePanel() {
        return new TimeOfUsePanel(this.driverExt, getPanelList().getListOfPanelNames().get(9));
    }
    
    public DeviceConfigPanel getDeviceConfigPanel() {
        return new DeviceConfigPanel(this.driverExt, getPanelList().getListOfPanelNames().get(10));
    }
    
    public EditMeterModal showMeterEditModal() {

        getMeterInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("meter-info-popup");

        return new EditMeterModal(this.driverExt, Optional.empty(), Optional.of("meter-info-popup"));
    }    
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {
        
        getAction().clickAndSelectOptionByText("Delete Meter");       
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
    
    public Panels getPanelList() {
    	return panels;
    }
    	
}
