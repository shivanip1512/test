package com.eaton.pages.demandresponse;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupDigiSepCreatePage extends LoadGroupCreatePage {

    public LoadGroupDigiSepCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }    
    
    private WebElement getDeviceClassSection() {
        Section deviceClassSection = new Section(this.driverExt, "Device Class");
        
        return deviceClassSection.getSection();
    }
    
    private WebElement getEnrollmentSection() {
        Section deviceClassSection = new Section(this.driverExt, "Enrollment");
        
        return deviceClassSection.getSection();
    }
    
    private WebElement getTimingSection() {
        Section deviceClassSection = new Section(this.driverExt, "Timing");
        
        return deviceClassSection.getSection();
    }    

    public DropDownMultiSelectElement getDeviceClass() {
        return new DropDownMultiSelectElement(this.driverExt, "deviceClassSet", getDeviceClassSection());
    }

    public TextEditElement getUtilityEnrollmentGroup() {
        return new TextEditElement(this.driverExt, "utilityEnrollmentGroup", getEnrollmentSection());
    }

    // Timing
    public TextEditElement getRampInTime() {
        return new TextEditElement(this.driverExt, "rampInMinutes", getTimingSection());
    }

    public TextEditElement getRampOutTime() {
        return new TextEditElement(this.driverExt, "rampOutMinutes", getTimingSection());
    }
}
