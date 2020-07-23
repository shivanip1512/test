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

public class LoadGroupVersacomCreatePage extends LoadGroupCreatePage {

    public LoadGroupVersacomCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
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
}

