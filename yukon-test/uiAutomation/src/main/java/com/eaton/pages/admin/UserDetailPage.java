package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserDetailPage extends PageBase {

    private Button edit;
    private Button changePassword;
    private Button unlockUser;

    public UserDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Admin.USER_DETAILS + id;
        
        edit = new Button(this.driverExt, "Edit");
        changePassword = new Button(this.driverExt, "Change Password");
        unlockUser = new Button(this.driverExt, "Unlock User");
    } 
    
    public UserDetailPage(DriverExtensions driverExt) {
        super(driverExt);

        edit = new Button(this.driverExt, "Edit");
        changePassword = new Button(this.driverExt, "Change Password");
        unlockUser = new Button(this.driverExt, "Unlock User");
    } 
    
    public Button getEdit() {
        return edit;
    }
    
    public Button getChangePassword() {
        return changePassword;
    }
    
    public Button getUnlockUser() {
        return unlockUser;
    }
}