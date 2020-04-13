package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserGroupDetailPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USER_GROUP_DETAILS;
    
    private Button edit;

    public UserGroupDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = DEFAULT_URL + id;

        edit = new Button(this.driverExt, "Edit");
    }  
    
    public UserGroupDetailPage(DriverExtensions driverExt) {
        super(driverExt);

        edit = new Button(this.driverExt, "Edit");
    } 
    
    public Button getEdit() {
        return edit;
    }    
}