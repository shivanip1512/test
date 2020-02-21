package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserDetailPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;
    
    private Button edit;
    private Button changePassword;
    private Button unlockUser;

    public UserDetailPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        //pageUrl = DEFAULT_URL;
        
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