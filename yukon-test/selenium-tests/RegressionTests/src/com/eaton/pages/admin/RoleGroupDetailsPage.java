package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class RoleGroupDetailsPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.ROLE_GROUP_DETAILS;
    
    private Button edit;

    public RoleGroupDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL + id;
        
        edit = new Button(this.driverExt, "Edit");
    } 
    
    public RoleGroupDetailsPage(DriverExtensions driverExt) {
        super(driverExt);

        edit = new Button(this.driverExt, "Edit");
    }
    
    public Button getEdit() {
        return edit;
    }    
}