package com.cannontech.web.editor.group;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.web.security.annotation.CheckRoleProperty;

//currently only used by LM group permission assignment
//additional possible uses should have associated role property requirements added when needed
@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class GroupEditorController extends MultiActionController {

    private YukonGroupDao yukonGroupDao;

    public YukonGroupDao getYukonGroupDao() {
        return yukonGroupDao;
    }

    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

    public ModelAndView editGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("group/editGroup.jsp");

        int groupId = ServletRequestUtils.getRequiredIntParameter(request, "groupId");
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        mav.addObject("group", group);
        mav.addObject("groupId", groupId);
        mav.addObject("groupName", group.getGroupName());

        return mav;
    }

}
