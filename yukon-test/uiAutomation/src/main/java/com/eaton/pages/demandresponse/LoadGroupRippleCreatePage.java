package com.eaton.pages.demandresponse;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnMultiSelectElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupRippleCreatePage extends LoadGroupCreatePage {

    public LoadGroupRippleCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }

    public DropDownElement getShedTime() {
        return new DropDownElement(this.driverExt, "shedTime");
    }
    
    public DropDownElement getGroup() {
        return new DropDownElement(this.driverExt, "group");
    }
  
    public DropDownElement getAreaCode() {
        return new DropDownElement(this.driverExt, "areaCode");
    }
    
    public SwitchBtnMultiSelectElement getControl() {
        WebElement section = getPageSection("Double Orders").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "js-control-address", section);
    }

    public SwitchBtnMultiSelectElement getRestore() {
        WebElement section = getPageSection("Double Orders").getSection();
        
        return new SwitchBtnMultiSelectElement(this.driverExt, "js-restore-address", section);
    }
}

