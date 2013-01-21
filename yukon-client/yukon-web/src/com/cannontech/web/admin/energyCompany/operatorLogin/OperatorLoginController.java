package com.cannontech.web.admin.energyCompany.operatorLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.validator.LoginPasswordValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginUsernameValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;

@RequestMapping("/energyCompany/operatorLogin/*")
@Controller
public class OperatorLoginController {
    
    private final String baseUrl = "/adminSetup/energyCompany/operatorLogin";
    
    @Autowired private AuthenticationService authenticationService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    private void checkPermissionsAndSetupModel(EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                               ModelMap modelMap,
                                               YukonUserContext userContext) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        
        if(!energyCompanyService.isParentOperator(userContext.getYukonUser().getUserID(), energyCompanyInfoFragment.getEnergyCompanyId())) {
            rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL, userContext.getYukonUser());
        }
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("currentUserId", userContext.getYukonUser().getUserID());
    }
    
    private LoginBackingBean loginBackingBeanFromYukonUser(LiteYukonUser user) {
        LoginBackingBean login = new LoginBackingBean();
        
        LiteUserGroup userResidentialUserGroup = userGroupDao.getLiteUserGroup(user.getUserGroupId());
        if (userResidentialUserGroup != null) {
            login.setUserGroupName(userResidentialUserGroup.getUserGroupName());
        }
        
        login.setLoginEnabled(user.getLoginStatus());
        login.setUserId(user.getUserID());
        login.setUsername(user.getUsername());
        
        return login;
    }
    
    @RequestMapping("home")
    public String home(YukonUserContext userContext, ModelMap modelMap, int ecId,  EnergyCompanyInfoFragment energyCompanyInfoFragment) {

        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        List<Integer> energyCompanyIds = Collections.singletonList(ecId);
        
        modelMap.addAttribute("operatorLogins", yukonUserDao.getOperatorLoginsByEnergyCompanyIds(energyCompanyIds));
        modelMap.addAttribute("currentUserId",userContext.getYukonUser().getLiteID());
        return "energyCompany/operatorLogin/home.jsp";
    }
    
    @RequestMapping("view")
    public String viewOperatorLogin(YukonUserContext userContext, ModelMap modelMap, int ecId, int operatorLoginId,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {

        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        LoginBackingBean login = loginBackingBeanFromYukonUser(yukonUserDao.getLiteYukonUser(operatorLoginId));
        
        modelMap.addAttribute("operatorLogin", login);
        modelMap.addAttribute("username", login.getUsername());
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/operatorLogin/create.jsp";
    }
    
    @RequestMapping("new")
    public String newOperatorLogin(YukonUserContext userContext,
                                   ModelMap modelMap,
                                   int ecId,
                                   EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        LoginBackingBean login = new LoginBackingBean();
        login.setLoginEnabled(true);
        modelMap.addAttribute("operatorLogin", login);
        modelMap.addAttribute("mode", PageEditMode.CREATE);
        
        //determine if we can set a password
        AuthType defaultAuthType = authenticationService.getDefaultAuthType();
        modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(defaultAuthType));
        modelMap.addAttribute("showOperatorGroupSelect", true);
        
        return "energyCompany/operatorLogin/create.jsp"; 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="save")
    public String createOperatorLogin(YukonUserContext userContext, ModelMap modelMap, int ecId,
                                      final @ModelAttribute ("operatorLogin") LoginBackingBean operatorLogin,
                                      BindingResult bindingResult, FlashScope flashScope,
                                      EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {

        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);

        AuthType defaultAuthType = authenticationService.getDefaultAuthType();

        //validate login
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(new LiteYukonUser());
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(new LiteYukonUser());

        passwordValidator.validate(operatorLogin, bindingResult);
        usernameValidator.validate(operatorLogin, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(defaultAuthType));
            modelMap.addAttribute("showOperatorGroupSelect", true);
            return "energyCompany/operatorLogin/create.jsp";
        }
        
        //save login
        UserGroup userGroup = userGroupDao.getDBUserGroupByUserGroupName(operatorLogin.getUserGroupName());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        StarsAdminUtil.createOperatorLogin(operatorLogin.getUsername(), operatorLogin.getPassword1(), 
                                            operatorLogin.getLoginStatus(), userGroup, energyCompany);

        //add message
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginCreated"));
        
        return "redirect:home";
    }
    
    @RequestMapping("edit")
    public String editOperatorLogin(YukonUserContext userContext, ModelMap model, int ecId, int operatorLoginId,
            EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        // check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, model, userContext);
        LoginBackingBean login = loginBackingBeanFromYukonUser(yukonUserDao.getLiteYukonUser(operatorLoginId));
        model.addAttribute("operatorLogin", login);
        model.addAttribute("username", login.getUsername());
        addEditFieldsToModel(model, operatorLoginId, login.getUserId());

        return "energyCompany/operatorLogin/create.jsp"; 
    }

    private void addEditFieldsToModel(ModelMap model, int operatorLoginId, int userId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        AuthenticationCategory authenticationCategory =
                yukonUserDao.getUserAuthenticationInfo(userId).getAuthenticationCategory();
        boolean supportsPasswordSet = authenticationService.supportsPasswordSet(authenticationCategory);
        model.addAttribute("supportsPasswordSet", supportsPasswordSet);
        model.addAttribute("isPrimaryOperator", yukonEnergyCompanyService.isPrimaryOperator(operatorLoginId));
        model.addAttribute("showOperatorGroupSelect", ecMappingDao.isOperatorInOperatorUserGroup(operatorLoginId));
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="update")
    public String updateOperatorLogin(YukonUserContext userContext, ModelMap model, int ecId,
                                      final @ModelAttribute ("operatorLogin") LoginBackingBean operatorLogin,
                                      BindingResult bindingResult, FlashScope flashScope,
                                      EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {

        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, model, userContext);
        
        //validate login
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(operatorLogin.getUserId());
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(user);
        usernameValidator.validate(operatorLogin, bindingResult);

        if (StringUtils.isNotBlank(operatorLogin.getPassword1()) && StringUtils.isNotBlank(operatorLogin.getPassword1())) {
            // Both are blank tells us the user doesn't want to change the password
            // Note we do not need to validate if they are blank because a blank password will not change the current password
            LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(user);
            passwordValidator.validate(operatorLogin, bindingResult);
        }
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            addEditFieldsToModel(model, operatorLogin.getUserId(), user.getUserID());

            return "energyCompany/operatorLogin/create.jsp";
        }
        
        //save login
        LiteYukonUser liteUser = yukonUserDao.getLiteYukonUser(operatorLogin.getUserId());
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserGroupName(operatorLogin.getUserGroupName());
        StarsAdminUtil.updateLogin(liteUser, operatorLogin.getUsername(), operatorLogin.getPassword1(),
            operatorLogin.getLoginStatus(), userGroup);
        
        //add message
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginUpdated"));
        
        return "redirect:home";
    }

    @RequestMapping(value="toggleOperatorLoginStatus",
                    method = {RequestMethod.POST, RequestMethod.HEAD},     
                    headers = "x-requested-with=XMLHttpRequest")
    public void toggleOperatorLoginStatus(YukonUserContext userContext,
                                            ModelMap modelMap,
                                            int operatorLoginId,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            EnergyCompanyInfoFragment energyCompanyInfoFragment) throws WebClientException, TransactionException, IOException {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        JSONObject returnJSON = new JSONObject();
        LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser(operatorLoginId);

        if (userContext.getYukonUser().getUserID() == operatorLoginId) {
            // The UI shouldn't allow this but just in case they get here send back a message
            returnJSON.put("message", new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.unableToDeleteCurrentUser").toString());
        } else {
            if(liteUser.getLoginStatus() == LoginStatusEnum.ENABLED) {
                liteUser.setLoginStatus(LoginStatusEnum.DISABLED);
                returnJSON.put("message", new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginStatusDisabled").toString());
            } else {
                liteUser.setLoginStatus(LoginStatusEnum.ENABLED);
                returnJSON.put("message", new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginStatusEnabled").toString());
            }
            LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(liteUser.getLiteID());
            
            // null here ensures password doesn't change
            StarsAdminUtil.updateLogin( liteUser, liteUser.getUsername(),null, liteUser.getLoginStatus(), userGroup);
        }
        returnJSON.put("loginStatus", liteUser.getLoginStatus());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        
        writer.write(returnJSON.toString());
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="delete")
    public String deleteOperatorLogin(YukonUserContext userContext,
                                      ModelMap modelMap,
                                      int ecId,
                                      final @ModelAttribute ("operatorLogin") LoginBackingBean operatorLogin,
                                      BindingResult bindingResult,
                                      FlashScope flashScope,
                                      EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        //delete user
        yukonUserDao.deleteUser(operatorLogin.getUserId());

        //add message
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginDeleted"));
        
        return "redirect:home";
    }
    
    @RequestMapping(value="availableUsername",
                    method = {RequestMethod.GET, RequestMethod.HEAD},     
                    headers = "x-requested-with=XMLHttpRequest")
    public void availableUsername (YukonUserContext userContext,
                                     ModelMap modelMap,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws WebClientException, TransactionException, IOException {
        StringWriter jsonDataWriter = new StringWriter();
        FileCopyUtils.copy(request.getReader(), jsonDataWriter);
        String jsonStr = jsonDataWriter.toString();
        JSONObject data = JSONObject.fromObject(jsonStr);
        String userName = data.getString("userName");
        
        JSONObject jsonUpdates = new JSONObject();
        LiteYukonUser duplicate = yukonUserDao.findUserByUsername(userName);
        if(duplicate != null) {
            jsonUpdates.put("valid", false);
        } else {
            jsonUpdates.put("valid", true);
        }
        
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        
        String responseJsonStr = jsonUpdates.toString();
        writer.write(responseJsonStr);
    }
    
    @ModelAttribute("assignableGroups")
    public List<LiteUserGroup> getAssignableGroups(int ecId) {
        return ecMappingDao.getOperatorUserGroups(ecId);
    }
    
    @ModelAttribute("baseUrl")
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    @ModelAttribute("loginStatusTypes")
    public LoginStatusEnum[] getLoginStatusTypes() {
        return LoginStatusEnum.values();
    }
}