package com.eaton.pages.demandresponse.loadgroup;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupCreatePage extends PageBase {
    
    private TextEditElement name;
    private DropDownElement type;
    private TextEditElement kWCapacity;
    private Button saveBtn;
    private Button cancelBtn;

    public LoadGroupCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
        
        name = new TextEditElement(this.driverExt, "name");
        type = new DropDownElement(this.driverExt, "type");
        kWCapacity = new TextEditElement(this.driverExt, "kWCapacity");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getType() {
        return type;
    }

    public TextEditElement getkWCapacity() {
        return kWCapacity;
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
        return saveBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public Section getPageSection(String sectionName) {
        return new Section(this.driverExt, sectionName);
    }    
}
