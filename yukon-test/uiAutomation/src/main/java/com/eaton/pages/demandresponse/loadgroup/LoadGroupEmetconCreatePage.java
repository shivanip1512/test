package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupEmetconCreatePage extends LoadGroupCreatePage {
    
    private DropDownElement communicationRoute;
    private TextEditElement goldAddress;
    private TextEditElement silverAddress;
    private RadioButtonElement addressToUse;
    private RadioButtonElement relayToUse;
    
    public LoadGroupEmetconCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        communicationRoute = new DropDownElement(this.driverExt, "routeId");
        goldAddress = new TextEditElement(this.driverExt, "goldAddress");
        silverAddress = new TextEditElement(this.driverExt, "silverAddress");
        addressToUse = new RadioButtonElement(this.driverExt, "addressUsage");
        relayToUse = new RadioButtonElement(this.driverExt, "relayUsage");
    }

    public DropDownElement getCommunicationRoute() {
        return communicationRoute;
    }
    
    public TextEditElement getGoldAddress() {
        return goldAddress;
    }
    
    public TextEditElement getSilverAddress() {
        return silverAddress;
    }
    
    public RadioButtonElement getAddressToUse() {
        return addressToUse;
    }
    
    public RadioButtonElement getRelayToUse() {
        return relayToUse;
    }
}
