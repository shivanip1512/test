package com.cannontech.web.editor.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.editor.UserGroupEditorControllerBase;

public class UserEditorController extends UserGroupEditorControllerBase<LiteYukonUser> {

    private YukonUserDao yukonUserDao;

    public UserEditorController() {
        super();
    }

    public YukonUserDao getYukonUserDao() {
        return yukonUserDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("/user/editUser");

        LiteYukonUser user = getAffected(request);
        mav.addObject("user", user);

        putPaosInModel(mav, user);

        return mav;
    }
    
    @Override
    protected LiteYukonUser getAffected(HttpServletRequest request) {
        String userId = request.getParameter("userId");

        LiteYukonUser user = yukonUserDao.getLiteYukonUser(Integer.valueOf(userId));
        
        return user;
    }

    @Override
    protected String getPickerId() {
        return "newUserPaoPicker";
    }
}
