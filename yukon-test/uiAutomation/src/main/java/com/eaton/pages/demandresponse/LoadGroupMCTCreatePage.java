package com.eaton.pages.demandresponse;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class LoadGroupMCTCreatePage extends LoadGroupCreatePage {
	


	    public LoadGroupMCTCreatePage(DriverExtensions driverExt) {
	        super(driverExt);

	        requiresLogin = true;
	        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
	    } 
	    
	    public TextEditElement getAddress() {
	        return new TextEditElement(this.driverExt, "address");
	    }
	    
	    public SwitchBtnMultiSelectElement getRelayMCT() {
	        WebElement section = getPageSection("Addressing").getSection();
	        
	        return new SwitchBtnMultiSelectElement(this.driverExt, "button-group", section);
	    }
	    
	    public DropDownElement getAddressLevel() {
	        return new DropDownElement(this.driverExt, "level");
	    }

	    private PickerElement getMCTAddressSelection() {
	        return new PickerElement(this.driverExt, "picker-mctMeterPicker");
	    }
	    
	    public SelectMCTMeterModal showAndWaitMCTMeter() {
	
	    	getMCTAddressSelection().clickButtonWithDynamicId();
	
	        SeleniumTestSetup.waitUntilModalVisibleByTitle("Select MCT Meter");
	
	        return new SelectMCTMeterModal(this.driverExt, Optional.empty(), Optional.of("mctMeterPicker"));
	    }
	        

	}



