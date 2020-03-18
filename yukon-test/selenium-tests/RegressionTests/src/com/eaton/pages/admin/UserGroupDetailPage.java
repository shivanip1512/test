package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserGroupDetailPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.ROLE_GROUP_DETAILS;
    
    private Button edit;

    public UserGroupDetailPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        
        edit = new Button(this.driverExt, "Edit");
    }    
    
    public Button getEdit() {
        return edit;
    }    
}