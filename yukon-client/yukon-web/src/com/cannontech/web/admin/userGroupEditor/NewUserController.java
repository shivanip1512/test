package com.cannontech.web.admin.userGroupEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightYukonUser;
import com.cannontech.common.user.NewUser;
import com.cannontech.common.user.Password;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.search.searcher.UserLuceneSearcher;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class NewUserController {
    
    @Autowired private AuthenticationService authService;
    @Autowired private YukonUserDao userDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private UserValidator userValidator;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private UserLuceneSearcher userLuceneSearcher;
    @Autowired private PasswordPolicyService passwordService;
    @Autowired private UsersEventLogService usersEventLogService;
    
    private final static String key = "yukon.web.modules.adminSetup.auth.user.";
    
    @RequestMapping("new-user-dialog")
    public String newUser(ModelMap model, YukonUserContext userContext) {
        
        EnergyCompany ec = ecDao.getEnergyCompany(userContext.getYukonUser());
        AuthenticationCategory category = authService.getDefaultAuthenticationCategory();
        model.addAttribute("showPw", authService.supportsPasswordSet(category));
        
        NewUser user = new NewUser();
        user.setAuthCategory(category);
        user.setLoginStatus(LoginStatusEnum.DISABLED);
        user.setEnergyCompanyId(ec.getId());
        model.addAttribute("user", user);
        
        model.addAttribute("categories", AuthenticationCategory.values());
        model.addAttribute("status", LoginStatusEnum.values());
        model.addAttribute("userGroups", userGroupDao.getAllLiteUserGroups());
        model.addAttribute("companies", ecDao.getAllEnergyCompanies());
        
        return "userGroupEditor/new-user.jsp";
    }
    
    @RequestMapping(value="users", method=RequestMethod.POST)
    public String create(ModelMap model, HttpServletResponse resp, 
            @ModelAttribute("user") NewUser user, BindingResult binding, LiteYukonUser createdBy) throws Exception {

        userValidator.validate(user, binding);
        
        AuthenticationCategory category = user.getAuthCategory();
        if (authService.supportsPasswordSet(category)) {
            // This is an admin setting a password for another user so
            // just verify it's not blank and the confirm matches. 
            // The user will be required to change it immediatley upon
            // logging in for the first time.
            Password pw = user.getPassword();
            if (StringUtils.isBlank(pw.getPassword())) {
                binding.rejectValue("password.password", key + "password.required");
            } else {
                if (!pw.getPassword().equals(pw.getConfirmPassword())) {
                    binding.rejectValue("password.confirmPassword", key + "password.mismatch");
                }
            }
        }
        
        if (binding.hasErrors()) {
            model.addAttribute("showPw", authService.supportsPasswordSet(category));
            model.addAttribute("categories", AuthenticationCategory.values());
            model.addAttribute("status", LoginStatusEnum.values());
            model.addAttribute("userGroups", userGroupDao.getAllLiteUserGroups());
            model.addAttribute("companies", ecDao.getAllEnergyCompanies());
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            return "userGroupEditor/new-user.jsp";
        }
        // Force reset only when Yukon is managing the password
        boolean forceReset = user.getAuthCategory() == AuthenticationCategory.ENCRYPTED;
        LiteYukonUser lyu = userDao.create(user, forceReset, createdBy);

        String groupName = CtiUtilities.STRING_NONE;
        String energyCompanyName = CtiUtilities.STRING_NONE;
        if (lyu.getUserGroupId() != null) {
            groupName = userGroupDao.getUserGroup(lyu.getUserGroupId()).getUserGroup().getUserGroupName();
        }
        if (user.getEnergyCompanyId() != null) {
            EnergyCompany energyCompany = ecDao.getEnergyCompany(user.getEnergyCompanyId());
            energyCompanyName = energyCompany.getName();
        }
        usersEventLogService.userCreated(user.getUsername(), groupName, energyCompanyName, user.getLoginStatus(),
            createdBy);

        if (lyu.getUserGroupId() != null) {
            usersEventLogService.userAdded(user.getUsername(), groupName, createdBy);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userId", lyu.getUserID());
        
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), result);
        
        return null;
    }
    
    @RequestMapping("username/available")
    public @ResponseBody Map<String, Object> username(String q) {
        
        boolean available = true;
        
        // Our searcher orders the results by score so an exact match should be the top
        // result and at least in the top five.
        SearchResults<UltraLightYukonUser> results = userLuceneSearcher.search(q, null, 0, 5);
        List<UltraLightYukonUser> names = results.getResultList();
        for (UltraLightYukonUser name : names) {
            if (q.equalsIgnoreCase(name.getUserName())) {
                available = false;
                break;
            }
        }
        
        return ImmutableMap.of("available", available);
    }
    
}