package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class AreaCreatePage extends PageBase {
    
    private TextEditElement name;
    private Button saveBtn;
    private Button cancelBtn;

    public AreaCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(driverExt, "name");
        saveBtn = new Button(driverExt, "Save");
        cancelBtn = new Button(driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
