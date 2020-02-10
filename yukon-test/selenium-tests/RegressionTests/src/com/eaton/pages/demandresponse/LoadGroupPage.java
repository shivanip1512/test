package com.eaton.pages.demandresponse;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class LoadGroupPage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;
    private static final String EDITLOADGROUPURL = "/dr/setup/filter?filterByType=LOAD_GROUP&name=AT+Edit+Emetcon+Ldgrp&_types=1&_types=1&_gearTypes=1&programIds=\r\n";
    private static final String COPYLOADGROUPURL = "";
    private static final String DELETELOADGROUPURL = "";

    public LoadGroupPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name", null);
        type = new DropDownElement(this.driver, "type", null);
    }
    
    public String getEditEmetconUrl() {
        return EDITLOADGROUPURL;
    }
    
    public String getCopyLoadGroupUrl() {
        return COPYLOADGROUPURL;
    }
    
    public String getDeleteLoadGroupUrl() {
        return DELETELOADGROUPURL;
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getType() {
        return type;
    }

    public DropDownElement getCommunicationRoute() {
        return new DropDownElement(this.driver, "routeId", null);
    }
    
    //Device Class
    public DropDownElement getDeviceClass() {
        return new DropDownElement(this.driver, "deviceClassSet", null);
    }
    
    //Enrollment
    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driver, "utilityEnrollmentGroup", null);
    }
    
    //Timing
    public TextEditElement getRampInTime() {
        return new TextEditElement(this.driver, "rampInMinutes", null);
    }
    
    public TextEditElement getRampOutTime() {
        return new TextEditElement(this.driver, "rampOutMinutes", null);
    }

    // Geographical Address
    public RadioButtonElement getAddressUsage() {
        // addressUsage is used for 2 different radio buttons on this page
        return new RadioButtonElement(this.driver, "addressUsage", null);
    }

    // Geographical Addressing
    public TextEditElement getSpid() {
        return new TextEditElement(this.driver, "serviceProvider", null);
    }

    public TextEditElement getGeo() {
        return new TextEditElement(this.driver, "geo", null);
    }

    public TextEditElement getSubstation() {
        return new TextEditElement(this.driver, "substation", null);
    }

    public RadioButtonElement getFeeder() {
        return new RadioButtonElement(this.driver, "feeder", null);
    }

    public TextEditElement getZip() {
        return new TextEditElement(this.driver, "zip", null);
    }

    public TextEditElement getUser() {
        return new TextEditElement(this.driver, "user", null);
    }

    public TextEditElement getSerial() {
        return new TextEditElement(this.driver, "serialNumber", null);
    }

    // Addressing
    public TextEditElement getGoldAddress() {
        return new TextEditElement(this.driver, "goldAddress", null);
    }

    public TextEditElement getSilverAddress() {
        return new TextEditElement(this.driver, "silverAddress", null);
    }

    public RadioButtonElement getAddressToUse() {
        return new RadioButtonElement(this.driver, "addressUsage", null);
    }

    public RadioButtonElement getRelayToUse() {
        return new RadioButtonElement(this.driver, "relayUsage", null);
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
        return new RadioButtonElement(this.driver, "addressUsage", null);
    }

    // LoadAddressing
    public RadioButtonElement getLoads() {
        return new RadioButtonElement(this.driver, "relayUsage", null);
    }

    public TextEditElement getProgram() {
        return new TextEditElement(this.driver, "program", null);
    }

    public TextEditElement getSplinter() {
        return new TextEditElement(this.driver, "splinter", null);
    }

    // Optional Attributes
    public DropDownElement getControlPriority() {
        return new DropDownElement(this.driver, "protocolPriority", null);
    }

    public TextEditElement getkWCapacity() {
        return new TextEditElement(this.driver, "kWCapacity", null);
    }

    public TrueFalseCheckboxElement getDisableGroup() {
        return new TrueFalseCheckboxElement(this.driver, "disableGroup", null);
    }

    public TrueFalseCheckboxElement getDisableControl() {
        return new TrueFalseCheckboxElement(this.driver, "disableControl", null);
    }

    public Button getSaveBtn() {
        return new Button(this.driver, "Save", null);
    }

    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel", null);
    }
}
