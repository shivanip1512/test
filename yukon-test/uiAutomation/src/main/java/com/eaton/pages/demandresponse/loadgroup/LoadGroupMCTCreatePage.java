package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class LoadGroupMCTCreatePage extends LoadGroupCreatePage {
    
    private DropDownElement communicationRoute;
    private PickerElement mctAddress;
    private TextEditElement address;
    private SwitchBtnMultiSelectElement relayUsage;
    private DropDownElement addressLevel;

    public LoadGroupMCTCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
                
        communicationRoute = new DropDownElement(this.driverExt, "routeId");
        addressLevel = new DropDownElement(this.driverExt, "level");
        mctAddress = new PickerElement(this.driverExt, Optional.empty(), Optional.of("mctMeterPicker"));
        address = new TextEditElement(this.driverExt, "address");        
        relayUsage = new SwitchBtnMultiSelectElement(this.driverExt, "button-group");        
    }
    
    public DropDownElement getCommunicationRoute() {
        return communicationRoute;
    }

    public PickerElement getMctAddress() {
        return mctAddress;
    }
    
    public String getMctAddressValidationMsg() {
        return getMctAddress().getValidationError("mctDeviceId");
    }

    public TextEditElement getAddress() {
        return address;
    }

    public SwitchBtnMultiSelectElement getRelayUsage() {
        return relayUsage;
    }

    public DropDownElement getAddressLevel() {
        return addressLevel;
    }

    public SelectMCTMeterModal showAndWaitMCTMeter() {        
        getMctAddress().clickLink();

        SeleniumTestSetup.waitUntilModalOpenByTitle("Select MCT Meter");

        return new SelectMCTMeterModal(this.driverExt, Optional.of("Select MCT Meter"), Optional.empty());
    }    
}
