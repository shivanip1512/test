package com.cannontech.web.editor.group;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.web.editor.UserGroupEditorControllerBase;

public class GroupEditorController extends UserGroupEditorControllerBase<LiteYukonGroup> {

    private YukonGroupDao yukonGroupDao;

    public GroupEditorController() {
        super();
    }

    public YukonGroupDao getYukonGroupDao() {
        return yukonGroupDao;
    }

    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

    public ModelAndView editGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("group/editGroup.jsp");

        LiteYukonGroup group = getAffected(request);
        mav.addObject("group", group);

        putPaosInModel(mav, group);

        return mav;
    }

    @Override
    protected LiteYukonGroup getAffected(HttpServletRequest request) {
        String groupId = request.getParameter("groupId");

        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(Integer.valueOf(groupId));

        return group;
    }

    @Override
    protected String getPickerId() {
        return "newGroupPaoPicker";
    }
}
