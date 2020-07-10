package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.CreateRoleGroupModal;
import com.eaton.elements.modals.CreateUserGroupModal;
import com.eaton.elements.modals.CreateUserModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.RoleGroupDetailsPage;
import com.eaton.pages.admin.UserDetailPage;
import com.eaton.pages.admin.UserGroupDetailPage;
import com.eaton.pages.admin.UsersAndGroupsPage;

public class UsersAndGroupsTests extends SeleniumTestSetup {
    
    private UsersAndGroupsPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.USERS_AND_GROUPS);

        page = new UsersAndGroupsPage(driverExt);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "User and Groups";
        
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void createUserSuccess() {
        CreateUserModal createModal = page.showAndWaitCreateUserModal();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "ATUser" + timeStamp;
        createModal.getUserName().setInputValue(name);
        createModal.getPassword().setInputValue("At12345!");
        createModal.getConfirmPassword().setInputValue("At12345!");
        createModal.getUserGroup().selectItemByText("QA Admin User Grp");
        createModal.getEnergyCompany().selectItemByText("QA_Test");
        
        createModal.clickOk();
        
        waitForUrlToLoad(Urls.Admin.USER_DETAILS, Optional.empty());

        UserDetailPage detailPage = new UserDetailPage(driverExt);

        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo("User (" + name + ")");
    }  
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void createRoleGroupSuccess() {
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
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void createUserGroupSuccess() {
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
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(page);
    }
}
