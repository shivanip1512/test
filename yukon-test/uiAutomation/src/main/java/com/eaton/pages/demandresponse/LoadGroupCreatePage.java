package com.eaton.pages.demandresponse;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupCreatePage extends PageBase {

    public LoadGroupCreatePage(DriverExtensions driverExt) {
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

    public SwitchBtnMultiSelectElement getRelayUsage() {
        WebElement section = getPageSection("Relay Usage").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
    }

    // LoadAddress
    public SwitchBtnMultiSelectElement getUsage() {
        return new SwitchBtnMultiSelectElement(this.driverExt, "loadaddressing");
    }

    // LoadAddressing
    public SwitchBtnMultiSelectElement getLoads() {
        WebElement section = getPageSection("Load Addressing").getSection();
        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
    }

    public TextEditElement getProgram() {
        return new TextEditElement(this.driverExt, "program");
    }

    public TextEditElement getSplinter() {
        return new TextEditElement(this.driverExt, "splinter");
    }

    // Optional Attributes
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

    public DropDownElement getRelay() {
        return new DropDownElement(this.driverExt, "virtualRelayId");
    }
}
