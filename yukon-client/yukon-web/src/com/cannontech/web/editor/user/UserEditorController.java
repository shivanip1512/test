package com.cannontech.web.editor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/user/*")
@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class UserEditorController extends MultiActionController {

    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthenticationService authenticationService;

    @RequestMapping
    public String editUser(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        
        AuthenticationThrottleDto authThrottleDto = authenticationService.getAuthenticationThrottleData(user.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("userId", user.getUserID());
        model.addAttribute("editingUsername", user.getUsername());  // Used by layout controller.
        model.addAttribute("authThrottleDto", authThrottleDto);
        return "user/editUser.jsp";
    }
    
    @RequestMapping
    public String removeLoginWait(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        
        authenticationService.removeAuthenticationThrottle(user.getUsername());

        model.addAttribute("user", user);
        return "user/editUser.jsp";
    }    
}