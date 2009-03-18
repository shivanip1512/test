package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetParameterHelper;

//currently only used by LM user permission assignment
//additional possible uses should have associated role property requirements added when needed
@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN}) 
public class UserPermissionEditorWidget extends UserGroupPermissionEditorWidget<LiteYukonUser> {
    private YukonUserDao yukonUserDao;

    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response)
    throws Exception {

        ModelAndView mav = new ModelAndView("user/editUser.jsp");

        LiteYukonUser user = getAffected(request);
        mav.addObject("user", user);

        putPaosInModel(request, mav, user);

        return mav;
    }

    @Override
    protected LiteYukonUser getAffected(HttpServletRequest request) throws Exception {
        int userId = WidgetParameterHelper.getRequiredIntParameter(request, "userId");

        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        
        return user;
    }
    
    @Override
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = super.identity(request, response);
        mav.addObject("userName", getAffected(request).getUsername());
        return mav;
    }
    
    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

}
