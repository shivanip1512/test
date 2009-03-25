package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.widget.support.WidgetParameterHelper;

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
