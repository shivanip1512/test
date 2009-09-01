package com.cannontech.web.editor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class UserGroupSelectorController extends MultiActionController {
    
    public ModelAndView userGroupSelector(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException {
        
        return new ModelAndView("userGroupSelector.jsp");
    }
}
