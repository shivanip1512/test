package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyOperatorUserCreatePage extends PageBase {
    
    private DropDownElement operatorGroup;
    private TrueFalseCheckboxElement loginEnabled;
    private TextEditElement userName;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    private Button generatePassword;
    //TODO show password needs to be fixed so it has the same checkbox properties as the loginEnabled
    //private TrueFalseCheckboxElement showPassword;
    
    private Button save;
    private Button cancel;
    
    public EnergyCompanyOperatorUserCreatePage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_CREATE + id;
        
        operatorGroup = new DropDownElement(this.driverExt, "userGroupName");
        loginEnabled = new TrueFalseCheckboxElement(this.driverExt, "loginEnabled");
        userName = new TextEditElement(this.driverExt, "username");
        password = new TextEditElement(this.driverExt, "password1");
        confirmPassword = new TextEditElement(this.driverExt, "password2");
        generatePassword = new Button(this.driverExt, "Generate Password");
        //showPassword = new TrueFalseCheckboxElement(this.driverExt, "");
        
        save = new Button(this.driverExt, "Save");
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
    
    public Button getCancelBtn() {
        return cancel;
    }
}
