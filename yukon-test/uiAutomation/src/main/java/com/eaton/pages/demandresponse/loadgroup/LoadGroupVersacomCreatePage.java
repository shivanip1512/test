package com.eaton.pages.demandresponse.loadgroup;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupVersacomCreatePage extends LoadGroupCreatePage {
    
    private DropDownElement communicationRoute;
    private SwitchBtnMultiSelectElement addressUsage;
    private TextEditElement utilityAddress;
    private TextEditElement sectionAddress;
    private TextEditElement serialAddress;

    public LoadGroupVersacomCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        communicationRoute = new DropDownElement(this.driverExt, "routeId");
        addressUsage = new SwitchBtnMultiSelectElement(this.driverExt, "verAddressUsage");
        utilityAddress = new TextEditElement(this.driverExt, "utilityAddress");
        sectionAddress = new TextEditElement(this.driverExt, "sectionAddress");
        serialAddress = new TextEditElement(this.driverExt, "serialAddress");        
    }

    public DropDownElement getCommunicationRoute() {
        return communicationRoute;
    }
    
    //Address Usage
    public SwitchBtnMultiSelectElement getAddressUsage() {
        
        return addressUsage;
    }

    // Addressing
    public TextEditElement getUtilityAddress() {
        return utilityAddress;
    }

    public TextEditElement getSectionAddress() {
        return sectionAddress;
    }

    public SwitchBtnMultiSelectElement getClassAddress() {
        WebElement section = getPageSection("Addressing").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "classAddress", section);
    }

    public SwitchBtnMultiSelectElement getDivisionAddress() {
        WebElement section = getPageSection("Addressing").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "divisionAddress", section);
    }

    public TextEditElement getSerialAddress() {
        return serialAddress;
    }
    
    public SwitchBtnMultiSelectElement getRelayUsage() {
        WebElement section = getPageSection("Relay Usage").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
    }
}

