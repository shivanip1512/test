package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class CapBankCreatePage  extends PageBase {
    
    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TrueFalseCheckboxElement createNewCbc;
    private Button saveBtn;
    private Button cancelBtn;

    public CapBankCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(driverExt, "name");
        status = new TrueFalseCheckboxElement(driverExt, "disabled");
        createNewCbc = new TrueFalseCheckboxElement(driverExt, "createCBC");
        saveBtn = new Button(driverExt, "Save");
        cancelBtn = new Button(driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public TrueFalseCheckboxElement getCreateNewCbc() {
        return createNewCbc;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
