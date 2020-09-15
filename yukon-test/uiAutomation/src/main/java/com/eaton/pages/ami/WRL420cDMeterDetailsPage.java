package com.eaton.pages.ami;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.elements.panels.DeviceConfigPanel;
import com.eaton.elements.panels.DeviceGroupsPanel;
import com.eaton.elements.panels.DisconnectPanel;
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

public class WRL420cDMeterDetailsPage extends PageBase {
	
	private Panels panels;
	
	public static final int METER_INFO_PANEL_INDEX = 0;
	public static final int METER_READINGS_PANEL_INDEX = 1;
	public static final int WIFI_CONNECTION_PANEL_INDEX = 2;
	public static final int NETWORK_INFO_PANEL_INDEX = 3;
	public static final int NOTES_PANEL_INDEX = 4;
	public static final int DEVICE_GROUP_PANEL_INDEX = 5;
	public static final int METER_TREND_PANEL_INDEX = 6;
	public static final int DISCONNECT_PANEL_INDEX = 7;
	public static final int METER_EVENTS_PANEL_INDEX = 8;
	public static final int OUTAGES_PANEL_INDEX = 9;
	public static final int TOU_PANEL_INDEX = 10;
	public static final int DEVICE_CONFIG_INDEX = 11;

    public WRL420cDMeterDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Ami.METER_DETAIL + id;
        panels = new Panels(driverExt);
    }
    
    public WRL420cDMeterDetailsPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getAction() {
        return new ActionBtnDropDownElement(this.driverExt);
    }

    public MeterInfoPanel getMeterInfoPanel() {
    	return new MeterInfoPanel(this.driverExt, panels.getPanels().get(METER_INFO_PANEL_INDEX).getPanelName());
    }
    
    public MeterReadingsPanel getMeterReadingsPanel() {
    	return new MeterReadingsPanel(this.driverExt, panels.getPanels().get(METER_READINGS_PANEL_INDEX).getPanelName());
    }
    
    public WiFiConnectionPanel getWiFiConnectionPanel() {
        return new WiFiConnectionPanel(this.driverExt, panels.getPanels().get(WIFI_CONNECTION_PANEL_INDEX).getPanelName());
    }
    
    public NetworkInfoPanel getNetworkInfoPanel() {
        return new NetworkInfoPanel(this.driverExt, panels.getPanels().get(NETWORK_INFO_PANEL_INDEX).getPanelName());
    }
    
    public NotesPanel getNotesPanel() {
        return new NotesPanel(this.driverExt, panels.getPanels().get(NOTES_PANEL_INDEX).getPanelName());
    }
    
    public DeviceGroupsPanel getDeviceGroupsPanel() {
        return new DeviceGroupsPanel(this.driverExt, panels.getPanels().get(DEVICE_GROUP_PANEL_INDEX).getPanelName());
    }
    
    public MeterTrendPanel getMeterTrendPanel() {
        return new MeterTrendPanel(this.driverExt, panels.getPanels().get(METER_TREND_PANEL_INDEX).getPanelName());
    }
    
    public DisconnectPanel getDisconnectPanel() {
        return new DisconnectPanel(this.driverExt, panels.getPanels().get(DISCONNECT_PANEL_INDEX).getPanelName());
    }
    
    public MeterEventsPanel getMeterEventsPanel() {
        return new MeterEventsPanel(this.driverExt, panels.getPanels().get(METER_EVENTS_PANEL_INDEX).getPanelName());
    }
    
    public OutagesPanel getOutagesPanel() {
        return new OutagesPanel(this.driverExt, panels.getPanels().get(OUTAGES_PANEL_INDEX).getPanelName());
    }
    
    public TimeOfUsePanel getTimeOfUsePanel() {
        return new TimeOfUsePanel(this.driverExt, panels.getPanels().get(TOU_PANEL_INDEX).getPanelName());
    }
    
    public DeviceConfigPanel getDeviceConfigPanel() {
        return new DeviceConfigPanel(this.driverExt, panels.getPanels().get(DEVICE_CONFIG_INDEX).getPanelName());
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
    
    public Panels getPanels() {
    	return panels;
    }
    	
}
