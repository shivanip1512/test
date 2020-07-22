package com.eaton.pages.demandresponse;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupRippleCreatePage extends PageBase {

    public LoadGroupRippleCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    // General
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }

    public DropDownElement getType() {
        return new DropDownElement(this.driverExt, "type");
    }

    public DropDownElement getCommunicationRoute() {
        return new DropDownElement(this.driverExt, "routeId");
    }
  
    public SwitchBtnMultiSelectElement getAddressUsage() {
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "verAddressUsage");
    }

    // Addressing
    public TextEditElement getUtilityAddress() {
        return new TextEditElement(this.driverExt, "utilityAddress");
    }

    public TextEditElement getSectionAddress() {
        return new TextEditElement(this.driverExt, "sectionAddress");
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
        return new TextEditElement(this.driverExt, "serialAddress");
    }
    
    public SwitchBtnMultiSelectElement getRelayUsage() {
        WebElement section = getPageSection("Relay Usage").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
    }

    public TextEditElement getkWCapacity() {
        return new TextEditElement(this.driverExt, "kWCapacity");
    }

    public SwitchBtnYesNoElement getDisableGroup() {
        WebElement section = getPageSection("Optional Attributes").getSection();
        
        return new SwitchBtnYesNoElement(this.driverExt, "disableGroup", section);
    }

    public SwitchBtnYesNoElement getDisableControl() {
        WebElement section = getPageSection("Optional Attributes").getSection();
        
        return new SwitchBtnYesNoElement(this.driverExt, "disableControl", section);
    }

    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }

    public Section getPageSection(String sectionName) {
        return new Section(this.driverExt, sectionName);
    }
}

