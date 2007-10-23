package com.cannontech.web.editor.group;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;

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

        return mav;
    }

}
