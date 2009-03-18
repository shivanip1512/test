package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetParameterHelper;

// currently only used by LM group permission assignment
// additional possible uses should have associated role property requirements added when needed
@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class GroupPermissionEditorWidget extends UserGroupPermissionEditorWidget<LiteYukonGroup> {
    private YukonGroupDao yukonGroupDao;

    @Override
    protected LiteYukonGroup getAffected(HttpServletRequest request) throws Exception {
        int groupId = WidgetParameterHelper.getRequiredIntParameter(request, "groupId");

        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);

        return group;
    }

    @Required
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Override
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = super.identity(request, response);
        mav.addObject("groupName", getAffected(request).getGroupName());
        return mav;
    }
}
