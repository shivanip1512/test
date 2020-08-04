package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupExpresscomCreatePage extends LoadGroupCreatePage {

    public LoadGroupExpresscomCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    // Geographical Address
    public SwitchBtnMultiSelectElement getGeographicalAddress() {

        return new SwitchBtnMultiSelectElement(this.driverExt, "addressUsage");
    }

    // Addressing
    public TextEditElement getSpid() {
        return new TextEditElement(this.driverExt, "serviceProvider");
    }

    public TextEditElement getGeo() {
        return new TextEditElement(this.driverExt, "geo");
    }

    public TextEditElement getSubstation() {
        return new TextEditElement(this.driverExt, "substation");
    }

    public SwitchBtnMultiSelectElement getFeeder() {
        WebElement section = getPageSection("Geographical Addressing").getSection();
        return new SwitchBtnMultiSelectElement(this.driverExt, "feederChk", section);
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

    // Load Address
    public SwitchBtnMultiSelectElement getLoadAddress() {

        return new SwitchBtnMultiSelectElement(this.driverExt, "loadaddressing");
    }

    // Load Addressing
    public String getSendLoadsInControlMessageText() {
        WebElement section = getPageSection("Load Addressing").getSection();
        return section.findElement(By.cssSelector("table .value span[class='']")).getText();
    }

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
    public DropDownElement getControlPriority() {
        return new DropDownElement(this.driverExt, "protocolPriority");
    }
}
