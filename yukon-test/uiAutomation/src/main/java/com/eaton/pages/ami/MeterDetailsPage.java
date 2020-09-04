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
import com.eaton.elements.panels.TimeOfUsePanel;
import com.eaton.elements.panels.WiFiConnectionPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class MeterDetailsPage extends PageBase {

    public MeterDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Ami.METER_DETAIL + id;
    }
    
    public MeterDetailsPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getAction() {
        return new ActionBtnDropDownElement(this.driverExt);
    }

    public MeterInfoPanel getMeterInfoPanel() {
        return new MeterInfoPanel(this.driverExt, "Meter Info");
    }
    
    public MeterReadingsPanel getMeterReadingsPanel() {
        return new MeterReadingsPanel(this.driverExt, "Meter Readings");
    }
    
    public WiFiConnectionPanel getWiFiConnectionPanel() {
        return new WiFiConnectionPanel(this.driverExt, "Wi-Fi Connection");
    }
    
    public NetworkInfoPanel getNetworkInfoPanel() {
        return new NetworkInfoPanel(this.driverExt, "Network Information");
    }
    
    public NotesPanel getNotesPanel() {
        return new NotesPanel(this.driverExt, "Notes");
    }
    
    public DeviceGroupsPanel getDeviceGroupsPanel() {
        return new DeviceGroupsPanel(this.driverExt, "Device Groups");
    }
    
    public MeterTrendPanel getMeterTrendPanel() {
        return new MeterTrendPanel(this.driverExt, "Meter Trend");
    }
    
    public DisconnectPanel getDisconnectPanel() {
        return new DisconnectPanel(this.driverExt, "Disconnect");
    }
    
    public MeterEventsPanel getMeterEventsPanel() {
        return new MeterEventsPanel(this.driverExt, "Meter Events");
    }
    
    public OutagesPanel getOutagesPanel() {
        return new OutagesPanel(this.driverExt, "Outages");
    }
    
    public TimeOfUsePanel getTimeOfUsePanel() {
        return new TimeOfUsePanel(this.driverExt, "Time of Use");
    }
    
    public DeviceConfigPanel getDeviceConfigPanel() {
        return new DeviceConfigPanel(this.driverExt, "Device Configuration");
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
}
