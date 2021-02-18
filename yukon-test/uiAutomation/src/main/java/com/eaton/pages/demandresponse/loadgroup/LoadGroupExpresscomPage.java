package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class LoadGroupExpresscomPage extends LoadGroupCreatePage {
    
    private DropDownElement communicationRoute;
    private SwitchBtnMultiSelectElement addressUsage;
    private TextEditElement spid;
    private TextEditElement geo;
    private TextEditElement substation;
    private SwitchBtnMultiSelectElement feeder;
    private TextEditElement zip;
    private TextEditElement user;
    private TextEditElement serial;
    private SwitchBtnMultiSelectElement usage;
    private TextEditElement program;
    private TextEditElement splinter;    
    private DropDownElement controlPriority;

    public LoadGroupExpresscomPage(DriverExtensions driverExt, String Url) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Url;
        
        communicationRoute = new DropDownElement(this.driverExt, "routeId");
        addressUsage = new SwitchBtnMultiSelectElement(this.driverExt, "addressUsage"); 
        spid = new TextEditElement(this.driverExt, "serviceProvider");
        geo = new TextEditElement(this.driverExt, "geo");
        substation = new TextEditElement(this.driverExt, "substation");
        feeder = new SwitchBtnMultiSelectElement(this.driverExt, "feederChk");
        zip = new TextEditElement(this.driverExt, "zip");
        user = new TextEditElement(this.driverExt, "user");
        serial = new TextEditElement(this.driverExt, "serialNumber");
        usage = new SwitchBtnMultiSelectElement(this.driverExt, "loadaddressing");
        program = new TextEditElement(this.driverExt, "program");
        splinter = new TextEditElement(this.driverExt, "splinter");
        controlPriority = new DropDownElement(this.driverExt, "protocolPriority");
    }    
    
    public DropDownElement getCommunicationRoute() {
        return communicationRoute;
    }

    // Geographical Address
    public SwitchBtnMultiSelectElement getGeographicalAddress() {
        return addressUsage;
    }

    // Addressing
    public TextEditElement getSpid() {
        return spid;
    }

    public TextEditElement getGeo() {
        return geo;
    }

    public TextEditElement getSubstation() {
        return substation;
    }

    public SwitchBtnMultiSelectElement getFeeder() {
        return feeder;
    }

    public TextEditElement getZip() {
        return zip;
    }

    public TextEditElement getUser() {
        return user;
    }

    public TextEditElement getSerial() {
        return serial;
    }

    // Load Address
    public SwitchBtnMultiSelectElement getUsage() {
        return usage;
    }

    // Load Addressing
    public String getSendLoadsInControlMessageText() {
        WebElement el = this.driverExt.findElement(By.cssSelector("span[id='js-sendControlMessageNo']"), Optional.of(3));
        String classAtr = el.getAttribute("class");
        if(classAtr.isBlank()) {
            return el.getText();
        } else {
            return this.driverExt.findElement(By.cssSelector("span[id='js-sendControlMessageYes']"), Optional.of(3)).getText();
        }
    }

    public SwitchBtnMultiSelectElement getLoads() {
        WebElement section = getPageSection("Load Addressing").getSection();

        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
    }

    public TextEditElement getProgram() {
        return program;
    }

    public TextEditElement getSplinter() {
        return splinter;
    }

    // Optional Attributes
    public DropDownElement getControlPriority() {
        return controlPriority;
    }
}
