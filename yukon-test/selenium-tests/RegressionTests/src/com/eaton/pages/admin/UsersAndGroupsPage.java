package com.eaton.pages.admin;

import com.eaton.elements.CreateBtnDropDownElement;
import com.eaton.elements.modals.CreateRoleGroupModal;
import com.eaton.elements.modals.CreateUserGroupModal;
import com.eaton.elements.modals.CreateUserModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UsersAndGroupsPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;
    private CreateBtnDropDownElement createBtn;
    private DriverExtensions driverExt;
    
    public UsersAndGroupsPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        this.driverExt = driverExt;
        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        createBtn = new CreateBtnDropDownElement(this.driverExt);
    }
    
    public CreateBtnDropDownElement getCreateBtn() {
        return createBtn;
    }
    
    public CreateUserModal showAndWaitCreateUserModal() {
        
        createBtn.clickAndSelectOptionByText("User");        
                      
        SeleniumTestSetup.waitUntilModalVisibleByTitle("New User");
        
        return new CreateUserModal(this.driverExt, "New User");        
    }
    
    public CreateUserGroupModal showAndWaitCreateUserGroupModal() {
        
        createBtn.clickAndSelectOptionByText("User Group");        
                      
        SeleniumTestSetup.waitUntilModalVisibleByTitle("New User Group");
        
        return new CreateUserGroupModal(this.driverExt, "New User Group");        
    }
    
    public CreateRoleGroupModal showAndWaitCreateRoleGroupModal() {
        
        createBtn.clickAndSelectOptionByText("Role Group");        
                      
        SeleniumTestSetup.waitUntilModalVisibleByTitle("New Role Group");
        
        return new CreateRoleGroupModal(this.driverExt, "New Role Group");        
    }
}
