package com.eaton.pages.demandresponse.loadgroup;

import org.openqa.selenium.WebElement;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupRfnExpresscomCreatePage extends LoadGroupCreatePage {
	private DropDownElement controlPriority;
	
    public LoadGroupRfnExpresscomCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        controlPriority = new DropDownElement(this.driverExt, "protocolPriority");
    }

    public DropDownElement getControlPriority() {
        return controlPriority;
    }
    
    // Address
    public SwitchBtnMultiSelectElement getAddressUsage() {
        WebElement section = getPageSection("Geographical Address").getSection();
        return new SwitchBtnMultiSelectElement(this.driverExt, "addressUsage", section);
    }

    // Addressing
    public TextEditElement getGeoAddress() {
        return new TextEditElement(this.driverExt, "geo");
    }

    public TextEditElement getSpidAddress() {
        return new TextEditElement(this.driverExt, "serviceProvider");
    }

    public TextEditElement getSubstationAddress() {
        return new TextEditElement(this.driverExt, "substation");
    }

    public TextEditElement getZipAddress() {
        return new TextEditElement(this.driverExt, "zip");
    }

    public TextEditElement getUserAddress() {
        return new TextEditElement(this.driverExt, "user");
    }

    public TextEditElement getSerialAddress() {
        return new TextEditElement(this.driverExt, "serialNumber");
    }

    public SwitchBtnMultiSelectElement getFeederAddress() {
        WebElement section = getPageSection("Geographical Addressing").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "feederChk", section);
    }

    // Load Address
    public SwitchBtnMultiSelectElement getLoadAddressUsage() {
        WebElement section = getPageSection("Load Address").getSection();
        return new SwitchBtnMultiSelectElement(this.driverExt, "loadaddressing", section);
    }

    public TextEditElement getProgramLoadAddress() {
        return new TextEditElement(this.driverExt, "program");
    }

    public TextEditElement getSplinterLoadAddress() {
        return new TextEditElement(this.driverExt, "splinter");
    }

    public SwitchBtnMultiSelectElement getLoads() {
        WebElement section = getPageSection("Load Addressing").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "loads", section);
    }

}