package com.cannontech.web.admin.energyCompany.operatorLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

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
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
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
import com.cannontech.web.stars.dr.operator.validator.LoginValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;

@RequestMapping("/energyCompany/operatorLogin/*")
@Controller
public class OperatorLoginController {
    
    private final String baseUrl = "/spring/adminSetup/energyCompany/operatorLogin";
    private AuthenticationService authenticationService;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private YukonGroupService yukonGroupService;
    private ECMappingDao ecMappingDao;
    private EnergyCompanyService energyCompanyService;
    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;
    private LoginValidatorFactory loginValidatorFactory;
    
    private void checkPermissionsAndSetupModel(EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                               ModelMap modelMap,
                                               YukonUserContext userContext) {
        
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        
        if(!energyCompanyService.isParentOperator(userContext.getYukonUser().getUserID(), energyCompanyInfoFragment.getEnergyCompanyId())) {
            rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL, userContext.getYukonUser());
        }
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
    }
    
    private LoginBackingBean loginBackingBeanFromYukonUser(LiteYukonUser user) {
        LoginBackingBean login = new LoginBackingBean();
        
        LiteYukonGroup userResidentialGroupName = yukonGroupService.getGroupByYukonRoleAndUser(YukonRole.CONSUMER_INFO, user.getUserID());
        if (userResidentialGroupName != null) {
            login.setLoginGroupName(userResidentialGroupName.getGroupName());
        }
        
        login.setAuthType(user.getAuthType());
        login.setLoginEnabled(user.getLoginStatus());
        login.setUserId(user.getUserID());
        login.setUsername(user.getUsername());
        
        return login;
    }
    
    @RequestMapping("home")
    public String home(YukonUserContext userContext, 
                       ModelMap modelMap, 
                       int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        List<Integer> energyCompanyIds = Collections.singletonList(ecId);
        
        modelMap.addAttribute("operatorLogins", yukonUserDao.getOperatorLoginsByEnergyCompanyIds(energyCompanyIds));
        return "energyCompany/operatorLogin/home.jsp";
    }
    
    @RequestMapping("view")
    public String viewOperatorLogin(YukonUserContext userContext, 
                       ModelMap modelMap, 
                       int ecId, 
                       int operatorLoginId,
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
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null );
        modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(defaultAuthType));
        
        return "energyCompany/operatorLogin/create.jsp"; 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="create")
    public String createOperatorLogin(YukonUserContext userContext,
                                      ModelMap modelMap,
                                      int ecId,
                                      final @ModelAttribute ("operatorLogin") LoginBackingBean operatorLogin,
                                      BindingResult bindingResult,
                                      FlashScope flashScope,
                                      EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null );
        
        //enforce the default AuthType for new logins
        operatorLogin.setAuthType(defaultAuthType);
        
        //validate login
        LoginValidator loginValidator = loginValidatorFactory.getLoginValidator(new LiteYukonUser());
        loginValidator.validate(operatorLogin, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(defaultAuthType));
            return "energyCompany/operatorLogin/create.jsp";
        }
        
        //save login
        LiteYukonGroup liteGroup = yukonGroupDao.getLiteYukonGroupByName(operatorLogin.getLoginGroupName());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        StarsAdminUtil.createOperatorLogin(operatorLogin.getUsername(), 
                                            operatorLogin.getPassword1(), 
                                            operatorLogin.getLoginStatus(), 
                                            new LiteYukonGroup[] {liteGroup}, 
                                            energyCompany);
        //add message
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginCreated"));
        
        return "redirect:home";
    }
    
    @RequestMapping("edit")
    public String editOperatorLogin(YukonUserContext userContext,
                                   ModelMap modelMap,
                                   int ecId,
                                   int operatorLoginId,
                                   EnergyCompanyInfoFragment energyCompanyInfoFragment) {
      //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        LoginBackingBean login = loginBackingBeanFromYukonUser(yukonUserDao.getLiteYukonUser(operatorLoginId));
        modelMap.addAttribute("operatorLogin", login);
        modelMap.addAttribute("username", login.getUsername());
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(yukonUserDao.getLiteYukonUser(login.getUserId()).getAuthType()));
        
        return "energyCompany/operatorLogin/create.jsp"; 
    }
    
    @RequestMapping(method=RequestMethod.POST, value="update", params="update")
    public String updateOperatorLogin(YukonUserContext userContext,
                                      ModelMap modelMap,
                                      int ecId,
                                      final @ModelAttribute ("operatorLogin") LoginBackingBean operatorLogin,
                                      BindingResult bindingResult,
                                      FlashScope flashScope,
                                      EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        //check permissions
        checkPermissionsAndSetupModel(energyCompanyInfoFragment, modelMap, userContext);
        
      //validate login
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(operatorLogin.getUserId());
        LoginValidator loginValidator = loginValidatorFactory.getLoginValidator(user);
        loginValidator.validate(operatorLogin, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(user.getAuthType()));
            return "energyCompany/operatorLogin/create.jsp";
        }
        
        //save login
        LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser(operatorLogin.getUserId());
        LiteYukonGroup loginGroup = yukonGroupDao.getLiteYukonGroupByName(operatorLogin.getLoginGroupName());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        StarsAdminUtil.updateLogin( liteUser, 
                                    operatorLogin.getUsername(),
                                    operatorLogin.getPassword1(),
                                    operatorLogin.getLoginStatus(),
                                    loginGroup,
                                    energyCompany,
                                    false);
        
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
        if(liteUser.getLoginStatus() == LoginStatusEnum.ENABLED) {
            liteUser.setLoginStatus(LoginStatusEnum.DISABLED);
            returnJSON.put("message", new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginStatusDisabled").toString());
        } else {
            liteUser.setLoginStatus(LoginStatusEnum.ENABLED);
            returnJSON.put("message", new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.operatorLogin.operatorLoginStatusEnabled").toString());
        }
        
        StarsAdminUtil.updateLogin( liteUser, 
                                    liteUser.getUsername(),
                                    "",
                                    liteUser.getLoginStatus(),
                                    null,
                                    null,
                                    false);
        
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
    public List<LiteYukonGroup> getAssignableGroups(int ecId) {
        return ecMappingDao.getOperatorGroups(ecId);
    }
    
    @ModelAttribute("baseUrl")
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    @ModelAttribute("loginStatusTypes")
    public LoginStatusEnum[] getLoginStatusTypes() {
        return LoginStatusEnum.values();
    }
    
    //DI
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setECMappingDao (ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setLoginValidatorFactory(LoginValidatorFactory loginValidatorFactory) {
        this.loginValidatorFactory = loginValidatorFactory;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setYukonGroupService(YukonGroupService yukonGroupService) {
        this.yukonGroupService = yukonGroupService;
    }
}
