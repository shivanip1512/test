package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateRoleGroupModal;
import com.eaton.elements.modals.CreateUserGroupModal;
import com.eaton.elements.modals.CreateUserModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.RoleGroupDetailsPage;
import com.eaton.pages.admin.UserDetailPage;
import com.eaton.pages.admin.UserGroupDetailPage;
import com.eaton.pages.admin.UsersAndGroupsPage;

public class UsersAndGroupsDetailsTests extends SeleniumTestSetup {
    
    private UsersAndGroupsPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        setRefreshPage(false);
        navigate(Urls.Admin.USERS_AND_GROUPS);
        page = new UsersAndGroupsPage(driverExt);        
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(page);    
        }
        
        setRefreshPage(false);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void userAndGroupsDetails_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "User and Groups";
        
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void userAndGroupsDetails_CreateUser_Success() {
        setRefreshPage(true);
        CreateUserModal createModal = page.showAndWaitCreateUserModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String userGroup = TestDbDataType.AdminData.QA_ADMIN_USERGROUP_ID.getId().toString();
        String energyCoId = TestDbDataType.EnergyCompanyData.EC_ID.getId().toString();
        
        String name = "ATUser" + timeStamp;
        createModal.getUserName().setInputValue(name);
        createModal.getPassword().setInputValue("At12345!");
        createModal.getConfirmPassword().setInputValue("At12345!");
        //42 = QA Admin User Grp
        createModal.getUserGroup().selectItemByValue(userGroup);
        //64 = QA_Test
        createModal.getEnergyCompany().selectItemByValue(energyCoId);
        
        createModal.clickOk();
        
        waitForUrlToLoad(Urls.Admin.USER_DETAILS, Optional.empty());

        UserDetailPage detailPage = new UserDetailPage(driverExt);

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo("User (" + name + ")");
    }  
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void userAndGroupsDetails_CreateRoleGroup_Success() {
        setRefreshPage(true);
        CreateRoleGroupModal createModal = page.showAndWaitCreateRoleGroupModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT Role Group " + timeStamp;
        createModal.getName().setInputValue(name);
        createModal.getDescription().setInputValue("Automated Tests " + timeStamp);
        
        createModal.clickOk();
        
        waitForUrlToLoad(Urls.Admin.ROLE_GROUP_DETAILS, Optional.empty());

        RoleGroupDetailsPage detailPage = new RoleGroupDetailsPage(driverExt);

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo("Role Group (" + name + ")");
    } 
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void userAndGroupsDetails_CreateUserGroup_Success() {
        setRefreshPage(true);
        CreateUserGroupModal createModal = page.showAndWaitCreateUserGroupModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT User Group " + timeStamp;
        createModal.getName().setInputValue(name);
        createModal.getDescription().setInputValue("Automated Tests " + timeStamp);
        
        createModal.clickOk();
        
        waitForUrlToLoad(Urls.Admin.USER_GROUP_DETAILS, Optional.empty());

        UserGroupDetailPage detailPage = new UserGroupDetailPage(driverExt);

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo("User Group (" + name + ")");
    }    
}
