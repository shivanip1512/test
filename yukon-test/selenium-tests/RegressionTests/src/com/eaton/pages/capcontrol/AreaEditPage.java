package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class AreaEditPage extends PageBase {
    
    private TextEditElement name;
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public AreaEditPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
        deleteBtn = new Button(this.driverExt, "Delete");
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
    
    public Button getDeleteBtn() {
        return deleteBtn;
    }
}
