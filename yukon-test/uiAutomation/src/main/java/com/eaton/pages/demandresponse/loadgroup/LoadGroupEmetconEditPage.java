package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupEmetconEditPage extends LoadGroupEditPage {
    private TextEditElement goldAddress;
    private TextEditElement silverAddress;
    private RadioButtonElement addressUsage;
    private RadioButtonElement relayUsage;
    private DropDownElement communicationRoute;

    public LoadGroupEmetconEditPage(DriverExtensions driverExt, int id) {
        super(driverExt, id);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;

        goldAddress = new TextEditElement(this.driverExt, "goldAddress");
        silverAddress = new TextEditElement(this.driverExt, "silverAddress");
        addressUsage = new RadioButtonElement(this.driverExt, "addressUsage", getPageSection("Addressing").getSection());
        relayUsage = new RadioButtonElement(this.driverExt, "relayUsage", getPageSection("Addressing").getSection());
        communicationRoute = new DropDownElement(this.driverExt, "routeId");
    }

    public TextEditElement getGoldAddress() {
        return goldAddress;
    }

    public TextEditElement getSilverAddress() {
        return silverAddress;
    }

    public RadioButtonElement getaddressUsage() {
        return addressUsage;
    }

    public RadioButtonElement getaddressrelayUsage() {
        return relayUsage;
    }

    public DropDownElement getCommuncationRoute() {
        return communicationRoute;
    }

}