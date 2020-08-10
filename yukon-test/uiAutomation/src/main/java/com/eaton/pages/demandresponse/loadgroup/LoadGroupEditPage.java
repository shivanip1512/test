package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupEditPage extends PageBase {

    private TextEditElement name;
    private TextEditElement kWCapacity;
    private Button saveBtn;
    private Button cancelBtn;

    public LoadGroupEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        kWCapacity = new TextEditElement(this.driverExt, "kWCapacity");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    //Optional Attributes
    public TextEditElement getkWCapacity() {
        return kWCapacity;
    }

    public SwitchBtnYesNoElement getDisableControl() {
        return new SwitchBtnYesNoElement(this.driverExt, "disableControl", getPageSection("Optional Attributes").getSection());
    }

    public SwitchBtnYesNoElement getDisableGroup() {
        return new SwitchBtnYesNoElement(this.driverExt, "disableGroup", getPageSection("Optional Attributes").getSection());
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
