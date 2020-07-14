package com.eaton.pages.demandresponse;

import java.util.List;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupCreatePage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;
    private static final String ADDRESS_USAGE = "addressUsage";

    public LoadGroupCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;

        name = new TextEditElement(this.driverExt, "name");
        type = new DropDownElement(this.driverExt, "type");
    }    

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getType() {
        return type;
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
        return new RadioButtonElement(this.driverExt, ADDRESS_USAGE);
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
        return new RadioButtonElement(this.driverExt, ADDRESS_USAGE);
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
        return new RadioButtonElement(this.driverExt, ADDRESS_USAGE);
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
    
    public Section getSection(String sectionName) {
        return new Section(this.driverExt, sectionName);
    }
}
