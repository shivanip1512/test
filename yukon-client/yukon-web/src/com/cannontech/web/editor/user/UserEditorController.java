package com.cannontech.web.editor.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class UserEditorController extends MultiActionController {

    private YukonUserDao yukonUserDao;
    private AuthenticationService authenticationService;

    public UserEditorController() {
        super();
    }

    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("user/editUser.jsp");

        int userId = ServletRequestUtils.getRequiredIntParameter(request, "userId");
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        mav.addObject("user", user);
        mav.addObject("username", user.getUsername());
        mav.addObject("userId", user.getUserID());

        AuthenticationThrottleDto authThrottleDto = authenticationService.getAuthenticationThrottleData(user.getUsername());
        mav.addObject("authThrottleDto", authThrottleDto);
        return mav;
    }
    
    public ModelAndView removeLoginWait(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {

        ModelAndView mav = new ModelAndView("user/editUser.jsp");

        int userId = ServletRequestUtils.getRequiredIntParameter(request, "userId");
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        mav.addObject("user", user);

        authenticationService.removeAuthenticationThrottle(user.getUsername());
        return mav;
    }    
    
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
