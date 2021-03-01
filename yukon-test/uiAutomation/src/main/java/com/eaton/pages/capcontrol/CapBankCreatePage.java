package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CapBankCreatePage  extends PageBase {
    
    public CapBankCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CAP_BANK_CREATE;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(driverExt, "name");
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(driverExt, "disabled");
    }
    
    public TrueFalseCheckboxElement getCreateNewCbc() {
        return new TrueFalseCheckboxElement(driverExt, "createCBC");
    }
    
    public Button getSaveBtn() {
        return new Button(driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(driverExt, "Cancel");
    }    
}
