package com.eaton.pages.demandresponse;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

    // Device Class
    public DropDownElement getDeviceClass() {
        return new DropDownElement(this.driverExt, "deviceClassSet");
    }

    // Enrollment
    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driverExt, "utilityEnrollmentGroup");
    }

    // Timing
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

    public TextEditElement getUtilityAddress() {
        return new TextEditElement(this.driverExt, "utilityAddress");
    }

    public TextEditElement getSectionAddress() {
        return new TextEditElement(this.driverExt, "sectionAddress");
    }

    public TextEditElement getClassAddress() {
        return new TextEditElement(this.driverExt, "classAddress");
    }

    public TextEditElement getDivisionAddress() {
        return new TextEditElement(this.driverExt, "divisionAddress");
    }

    public TextEditElement getSerialAddress() {
        return new TextEditElement(this.driverExt, "serialAddress");
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

    public Section getSection(String sectionName) {
        return new Section(this.driverExt, sectionName);
    }

    public void clickSectionSwitchButtonsByName(String sectionName, String buttonName, String sectionLabel) {
        List<WebElement> nameElements;
        if (sectionName.contentEquals("Optional Attributes")) {
            nameElements = getSection(sectionName).getSection()
                    .findElements(By.cssSelector("td.value input[name='" + sectionLabel + "']~label>span"));
        } else if (sectionName.contentEquals("Addressing")) {
            nameElements = getSection(sectionName).getSection()
                    .findElements(By.cssSelector("table input[name='" + sectionLabel + "']+div span>span"));
        } else {
            nameElements = getSection(sectionName).getSection().findElements(By.cssSelector(".button-group span>span"));
        }

        for (WebElement element : nameElements) {
            if (element.getText().contentEquals(buttonName)) {
                element.click();
                break;
            }
        }
    }

    public String getAddressingSectionSwitchButtonStatusByLabelName(String sectionLabel) {
        WebElement nameElement;
        if (sectionLabel != "sectionAddress" && sectionLabel != "serialAddress") {
            nameElement = getSection("Addressing").getSection()
                    .findElement(By.cssSelector("table input[name='" + sectionLabel + "']~div label>input"));
        } else {
            nameElement = getSection("Addressing").getSection()
                    .findElement(By.cssSelector("table input[name='" + sectionLabel + "']"));
        }
        if (nameElement.getAttribute("disabled") == null) {
            return "false";
        } else {
            return "true";
        }
    }
}
