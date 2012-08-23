package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class GroupPermissionEditorWidget extends UserGroupPermissionEditorWidget<LiteUserGroup> {
    private UserGroupDao userGroupDao;

    @Override
    protected LiteUserGroup getAffected(HttpServletRequest request) throws Exception {
        int userGroupId = WidgetParameterHelper.getRequiredIntParameter(request, "userGroupId");

        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);

        return userGroup;
    }

    @Override
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = super.identity(request, response);
        mav.addObject("userGroupName", getAffected(request).getUserGroupName());
        return mav;
    }

    @Required
    public void setUserGroupDao(UserGroupDao userGroupDao) {
        this.userGroupDao = userGroupDao;
    }
}