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

public class LoadGroupEditPage extends PageBase {

    private TextEditElement name;

    public LoadGroupEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
    }    

    public String getPageTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getCommunicationRoute() {
        return new DropDownElement(this.driver, "routeId");
    }
    
    //Device Class
    public DropDownElement getDeviceClass() {
        return new DropDownElement(this.driver, "deviceClassSet");
    }
    
    //Enrollment
    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driver, "utilityEnrollmentGroup");
    }
    
    //Timing
    public TextEditElement getRampInTime() {
        return new TextEditElement(this.driver, "rampInMinutes");
    }
    
    public TextEditElement getRampOutTime() {
        return new TextEditElement(this.driver, "rampOutMinutes");
    }

    // Geographical Address
    public RadioButtonElement getAddressUsage() {
        // addressUsage is used for 2 different radio buttons on this page
        return new RadioButtonElement(this.driver, "addressUsage");
    }

    // Geographical Addressing
    public TextEditElement getSpid() {
        return new TextEditElement(this.driver, "serviceProvider");
    }

    public TextEditElement getGeo() {
        return new TextEditElement(this.driver, "geo");
    }

    public TextEditElement getSubstation() {
        return new TextEditElement(this.driver, "substation");
    }

    public RadioButtonElement getFeeder() {
        return new RadioButtonElement(this.driver, "feeder");
    }

    public TextEditElement getZip() {
        return new TextEditElement(this.driver, "zip");
    }

    public TextEditElement getUser() {
        return new TextEditElement(this.driver, "user");
    }

    public TextEditElement getSerial() {
        return new TextEditElement(this.driver, "serialNumber");
    }

    // Addressing
    public TextEditElement getGoldAddress() {
        return new TextEditElement(this.driver, "goldAddress");
    }

    public TextEditElement getSilverAddress() {
        return new TextEditElement(this.driver, "silverAddress");
    }

    public RadioButtonElement getAddressToUse() {
        return new RadioButtonElement(this.driver, "addressUsage");
    }

    public RadioButtonElement getRelayToUse() {
        return new RadioButtonElement(this.driver, "relayUsage");
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
        return new RadioButtonElement(this.driver, "addressUsage");
    }

    // LoadAddressing
    public RadioButtonElement getLoads() {
        return new RadioButtonElement(this.driver, "relayUsage");
    }

    public TextEditElement getProgram() {
        return new TextEditElement(this.driver, "program");
    }

    public TextEditElement getSplinter() {
        return new TextEditElement(this.driver, "splinter");
    }

    // Optional Attributes
    public DropDownElement getControlPriority() {
        return new DropDownElement(this.driver, "protocolPriority");
    }

    public TextEditElement getkWCapacity() {
        return new TextEditElement(this.driver, "kWCapacity");
    }

    public TrueFalseCheckboxElement getDisableGroup() {
        return new TrueFalseCheckboxElement(this.driver, "disableGroup");
    }

    public TrueFalseCheckboxElement getDisableControl() {
        return new TrueFalseCheckboxElement(this.driver, "disableControl");
    }

    public Button getSaveBtn() {
        return new Button(this.driver, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel");
    }
}
