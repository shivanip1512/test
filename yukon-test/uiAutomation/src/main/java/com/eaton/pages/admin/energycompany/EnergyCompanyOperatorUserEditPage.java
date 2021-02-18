package com.eaton.pages.admin.energycompany;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyOperatorUserEditPage extends PageBase {
    
    private DropDownElement operatorGroup;
    private TrueFalseCheckboxElement loginEnabled;
    private TextEditElement userName;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    private Button generatePassword;
    //TODO show password needs to be fixed so it has the same checkbox properties as the loginEnabled
    //private TrueFalseCheckboxElement showPassword;
    
    private Button save;
    private Button delete;
    private Button cancel;
    
    public EnergyCompanyOperatorUserEditPage(DriverExtensions driverExt, int energyCompanyId, int loginId) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + energyCompanyId + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + loginId;
        
        operatorGroup = new DropDownElement(this.driverExt, "userGroupName");
        loginEnabled = new TrueFalseCheckboxElement(this.driverExt, "loginEnabled");
        userName = new TextEditElement(this.driverExt, "username");
        password = new TextEditElement(this.driverExt, "password1");
        confirmPassword = new TextEditElement(this.driverExt, "password2");
        generatePassword = new Button(this.driverExt, "Generate Password");
        //showPassword = new TrueFalseCheckboxElement(this.driverExt, "");
        
        save = new Button(this.driverExt, "Save");
        delete = new Button(this.driverExt, "Delete");
        cancel = new Button(this.driverExt, "Cancel");
    } 
    
    public DropDownElement getOperatorGroup() {
        return operatorGroup;
    }
    
    public TrueFalseCheckboxElement getLoginEnabled() {
        return loginEnabled;
    }
    
    public TextEditElement getUserName() {
        return userName;
    }
    
    public TextEditElement getPassword() {
        return password;
    }
    
    public TextEditElement getConfirmPassword() {
        return confirmPassword;
    }
    
    public Button getGeneratePassword() {
        return generatePassword;
    }
    
    public Button getSaveBtn() {
        return save;
    }
    
    public  Button getDeleteBtn() {
        return delete;
    }
    
    public Button getCancelBtn() {
        return cancel;
    }
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {      
        getDeleteBtn().click();     
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
}
