package com.eaton.pages.demandresponse;

import java.util.List;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class LoadGroupEditPage extends PageBase {

    private TextEditElement name;

    public LoadGroupEditPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
    }    

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getCommunicationRoute() {
        return new DropDownElement(this.driverExt, "routeId");
    }
    
    //Device Class
    public DropDownElement getDeviceClass() {
        return new DropDownElement(this.driverExt, "deviceClassSet");
    }
    
    //Enrollment
    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driverExt, "utilityEnrollmentGroup");
    }
    
    //Timing
    public TextEditElement getRampInTime() {
        return new TextEditElement(this.driverExt, "rampInMinutes");
    }
    
    public TextEditElement getRampOutTime() {
        return new TextEditElement(this.driverExt, "rampOutMinutes");
    }

    // Geographical Address
    public RadioButtonElement getAddressUsage() {
        // addressUsage is used for 2 different radio buttons on this page
        return new RadioButtonElement(this.driverExt, "addressUsage");
    }

    // Geographical Addressing
    public TextEditElement getSpid() {
        return new TextEditElement(this.driverExt, "serviceProvider");
    }

    public TextEditElement getGeo() {
        return new TextEditElement(this.driverExt, "geo");
    }

    public TextEditElement getSubstation() {
        return new TextEditElement(this.driverExt, "substation");
    }

    public RadioButtonElement getFeeder() {
        return new RadioButtonElement(this.driverExt, "feeder");
    }

    public TextEditElement getZip() {
        return new TextEditElement(this.driverExt, "zip");
    }

    public TextEditElement getUser() {
        return new TextEditElement(this.driverExt, "user");
    }

    public TextEditElement getSerial() {
        return new TextEditElement(this.driverExt, "serialNumber");
    }

    // Addressing
    public TextEditElement getGoldAddress() {
        return new TextEditElement(this.driverExt, "goldAddress");
    }

    public TextEditElement getSilverAddress() {
        return new TextEditElement(this.driverExt, "silverAddress");
    }

    public RadioButtonElement getAddressToUse() {
        return new RadioButtonElement(this.driverExt, "addressUsage");
    }

    public RadioButtonElement getRelayToUse() {
        return new RadioButtonElement(this.driverExt, "relayUsage");
    }

    public List<String> getRelayToUseValues() {
        return getRelayToUse().getValues();
    }

    public List<String> getAddressToUseValues() {
        return getAddressToUse().getValues();
    }

    // LoadAddress
    public RadioButtonElement getUsage() {
        // addressUsage is used for 2 different radio buttons on this page
        return new RadioButtonElement(this.driverExt, "addressUsage");
    }

    // LoadAddressing
    public RadioButtonElement getLoads() {
        return new RadioButtonElement(this.driverExt, "relayUsage");
    }

    public TextEditElement getProgram() {
        return new TextEditElement(this.driverExt, "program");
    }

    public TextEditElement getSplinter() {
        return new TextEditElement(this.driverExt, "splinter");
    }

    // Optional Attributes
    public DropDownElement getControlPriority() {
        return new DropDownElement(this.driverExt, "protocolPriority");
    }

    public TextEditElement getkWCapacity() {
        return new TextEditElement(this.driverExt, "kWCapacity");
    }

    public TrueFalseCheckboxElement getDisableGroup() {
        return new TrueFalseCheckboxElement(this.driverExt, "disableGroup");
    }

    public TrueFalseCheckboxElement getDisableControl() {
        return new TrueFalseCheckboxElement(this.driverExt, "disableControl");
    }

    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }
}
