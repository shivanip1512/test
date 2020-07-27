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

public class LoadGroupRFNExpressCommCreatePage extends PageBase {

    public LoadGroupRFNExpressCommCreatePage(DriverExtensions driverExt) {
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

	    // Address
	    public SwitchBtnMultiSelectElement getAddressUsage() {
	        
	        return new SwitchBtnMultiSelectElement(this.driverExt, "addressUsage");
	    }

	    // Addressing
	    public TextEditElement getGeoAddress() {
	        return new TextEditElement(this.driverExt, "geo");
	    }

	    public TextEditElement getSpidAddress() {
	        return new TextEditElement(this.driverExt, "spid");
	    }
	    
	    public TextEditElement getSubstationAddress() {
	        return new TextEditElement(this.driverExt, "substation");
	    }

	    public TextEditElement getZipAddress() {
	        return new TextEditElement(this.driverExt, "zip");
	    }
	    
	    public TextEditElement getUserAddress() {
	        return new TextEditElement(this.driverExt, "user");
	    }
	    
	    public TextEditElement getSerialAddress() {
	        return new TextEditElement(this.driverExt, "serial");
	    }
	    
	    public SwitchBtnMultiSelectElement getFeederAddress() {
	        WebElement section = getPageSection("Geographical Addressing").getSection();
	        
	        return new SwitchBtnMultiSelectElement(this.driverExt, "feeder", section);
	    }

	    // Load Address
	    public SwitchBtnMultiSelectElement getLoadAddressUsage() {
	        
	        return new SwitchBtnMultiSelectElement(this.driverExt, "loadaddressing");
	    }
	    
	    public TextEditElement getProgramLoadAddress() {
	        return new TextEditElement(this.driverExt, "program");
	    }
	    
	    public TextEditElement getSplinterLoadAddress() {
	        return new TextEditElement(this.driverExt, "splinter");
	    }
	    
	    public SwitchBtnMultiSelectElement getLoads() {
	        WebElement section = getPageSection("Load Addressing").getSection();
	        
	        return new SwitchBtnMultiSelectElement(this.driverExt, "loads", section);
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
	    
	    
	    public TextEditElement getFieldValidationError(String elementName) {
	    	return new TextEditElement(this.driverExt, elementName);
	    }
	}